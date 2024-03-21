import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../../css/AddUser.css";
import handleFormError from "../../utils/handleFormError";
import ErrorForm from "../../pages/errors/ErrorForm";


function UserForm({ title }) {
  const navigate = useNavigate();

  const [error, setError] = useState({ title: null, message: null });
  const [gettingError, setGettingError] = useState(false);

  const [roles, setRoles] = useState({});
  const [selectedRoles, setSelectedRoles] = useState([]);
  const [organizations, setOrganizations] = useState([]);
  const [orgUnits, setOrgUnits] = useState([]);
  const [selectedOrg, setSelectedOrg] = useState("");
  const [userInfo, setUserInfo] = useState({
    id: "",
    lastName: "",
    firstName: "",
    email: "",
    phoneNumber: "",
    job: "",
    orgUnit: "",
  });

  useEffect(() => {
    fetchRoles();
    fetchOrgUnits();
  }, []);

  useEffect(() => {
    if (selectedOrg) {
      setOrgUnits(selectedOrg.orgUnits);
    } else {
      setOrgUnits([]);
    }
  }, [selectedOrg]);

  const fetchRoles = async () => {
    const apiUrl = "https://localhost:8041/api/authentication/v1/roles";
    try {
      const response = await fetch(apiUrl, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("Token")}`,
        },
      });
      if (!response.ok) {
        if (response.status === 401) { navigate("/error401"); }
        else if (response.status === 403) { navigate("/error403"); }
        else { throw new Error(response.status + " " + response.statusText); }
      }
      const data = await response.json();
      setRoles(data);
    } catch (error) {
      console.error("API request error: ", error);
    }
  };

  const fetchOrgUnits = async () => {
    const apiUrl = "https://localhost:8041/api/directory/v1/organisations";
    try {
      const response = await fetch(apiUrl, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("Token")}`,
        },
      });
      if (!response.ok) {
        if (response.status === 401) { navigate("/error401"); }
        else if (response.status === 403) { navigate("/error403"); }
        else { throw new Error(response.status + " " + response.statusText); }
      }
      const data = await response.json();
      setOrganizations(data);
    } catch (error) {
      console.error("API request error: ", error);
    }
  };

  const handleInfoChange = (fieldName, value) => {
    setUserInfo((prevUserInfo) => ({
      ...prevUserInfo,
      [fieldName]: value,
    }));
  };

  const handleRoleChange = (role, checked) => {
    if (checked) {
      setSelectedRoles((prevSelectedRoles) => [...prevSelectedRoles, role]);
    } else {
      setSelectedRoles((prevSelectedRoles) =>
        prevSelectedRoles.filter((selectedRole) => selectedRole !== role),
      );
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch(
        "https://localhost:8041/api/authentication/v1/login-profiles",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${localStorage.getItem("Token")}`,
          },
          body: JSON.stringify({ email: userInfo.email, roles: selectedRoles }),
        },
      );

      // Handle error using handleFormError function
      const [getError, error] = await handleFormError(response, navigate);
      if (getError) {
        setGettingError(true);
        setError(error);
        return; // Return early to stop further execution
      }


      const data = await response.json();
      setUserInfo((prevUserInfo) => ({
        ...prevUserInfo,
        id: data.id,
      }));

      // Handle success response
      console.log(
        "User email submitted successfully for login-profile creation",
      );

      try {
        const secondResponse = await fetch(
          "https://localhost:8041/api/directory/v1/employees",
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${localStorage.getItem("Token")}`,
            },
            body: JSON.stringify({
              ...userInfo, // Include updated userInfo
              id: data.id, // Use the id from data
            }),
          },
        );

        // Handle error using handleFormError function
        const [getError, error] = await handleFormError(response, navigate);
        if (getError) {
          setGettingError(true);
          setError(error);
          return; // Return early to stop further execution
        }

        // Handle success response for the second request
        console.log("User creation request successful");
      } catch (error) {
        console.error("Error submitting user email:", error);
      }
    } catch (error) {
      console.error("Error submitting user email:", error);
    }
  };

  // Function to transform camelCase string to title case with spaces
  const camelToTitleCase = (str) => {
    // Insert a space before all caps
    const spacedStr = str.replace(/([A-Z])/g, " $1");
    // Capitalize the first letter and lowercase the rest
    return spacedStr.charAt(0).toUpperCase() + spacedStr.slice(1).toLowerCase();
  };

  return (
    <>
      <div className="user-form-container">
        <div className="title">{title}</div>
        <form className="add-user-form" onSubmit={handleSubmit}>
          {Object.entries(userInfo)
            .filter(([key]) => key !== "orgUnit")
            .filter(([key]) => key !== "id")
            .map(([fieldName, fieldValue]) => (
              <div key={fieldName} className="input-container">
                <label className="add-user-label">
                  {camelToTitleCase(fieldName)}
                </label>
                <input
                  className="add-user-input"
                  type="text"
                  value={fieldValue}
                  onChange={(e) => handleInfoChange(fieldName, e.target.value)}
                />
              </div>
            ))}
          <div className="input-container">
            <label className="add-user-label">Organization</label>
            <select
              className="add-user-input"
              value={selectedOrg}
              onChange={(e) => {
                const orgId = e.target.value;
                setSelectedOrg(organizations.find((org) => org.id == orgId));
              }}
            >
              {!selectedOrg && <option value="">Select Organization</option>}
              {organizations.map((org) => (
                <option key={org.id} value={org.id}>
                  {org.name}
                </option>
              ))}
            </select>
          </div>
          <div className="input-container">
            <label className="add-user-label">Organization Unit</label>
            <select
              className="add-user-input"
              value={userInfo.orgUnit}
              onChange={(e) => handleInfoChange("orgUnit", e.target.value)}
            >
              {!userInfo.orgUnit && (
                <option value="">Select Organization</option>
              )}
              {orgUnits.map((unit) => (
                <option key={unit.id} value={unit.id}>
                  {unit.name}
                </option>
              ))}
            </select>
          </div>
          <div className="select-role-container">
            <label className="add-user-label">Roles</label>
            <div className="select-role-button-container">
              {Array.isArray(roles) &&
                roles.map((role, index) => (
                  <div key={index} className="add-user-checkbox-item">
                    <input
                      className="add-user-input"
                      type="checkbox"
                      name={role}
                      checked={selectedRoles.includes(role)}
                      onChange={(e) => handleRoleChange(role, e.target.checked)}
                    />
                    <label className="add-user-roles">{role}</label>
                  </div>
                ))}
            </div>
          </div>
          <button className="add-user-button" onClick={handleSubmit}>
            Submit
          </button>
        </form>
        {gettingError && (
          <ErrorForm
            title={error.title}
            message={error.message}
            onClose={() => {
              setGettingError(false);
            }}
          />
        )}
      </div>
    </>
  );
}

export default UserForm;
