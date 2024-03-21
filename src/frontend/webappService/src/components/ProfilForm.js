import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "../css/App.css";
import "../css/ProfilForm.css";
import Button from "./Button";
import ErrorForm from "../pages/errors/ErrorForm";

function ProfilForm({ title, userId }) {
  const navigate = useNavigate();
  const [user, setUser] = useState({});
  const [modifiedUser, setModifiedUser] = useState({});
  const [orgUnits, setOrgUnits] = useState([]);
  const [organizations, setOrganizations] = useState([]);
  const [selectedOrg, setSelectedOrg] = useState(null);

  const tokenName = "Token"; // Need to be the same name as in AuthForm.js components
  const token = localStorage.getItem(tokenName);

  // Prepare the 'Authorization' header with the value 'Bearer' and the token
  const headers = {
    Authorization: `Bearer ${token}`,
  };

  // Get the user profil information
  useEffect(() => {
    getUserDir();
    getOrgUnits();
  }, []);

  // Get user from API Directory
  const getUserDir = async () => {
    // API URL
    const apiUrl =
      "https://localhost:8041/api/directory/v1/employees/" + userId;

    await fetch(apiUrl, {
      method: "GET",
      headers: headers,
    })
      .then((response) => {
        if (!response.ok) {
          if (response.status === 401) { navigate("/error401"); }
          else if (response.status === 403) { navigate("/error403"); }
          else if (response.status === 400 || response.status === 422) { <ErrorForm title={response.status} message={response.statusText} />; }
          else { throw new Error(response.status + " " + response.statusText); }
        }
        const res = response.json();
        return res;
      })
      .then((data) => {
        setUser((prevUser) => ({
          ...prevUser,
          Id: data.id,
          Lastname: data.lastName,
          Firstname: data.firstName,
          Email: data.email,
          "Phone Number": data.phoneNumber,
          Job: data.job,
          Organization: data.organisation?.name,
          "Organization Unit": data.orgUnit?.id,
        }));
        console.info("[DATA] " + JSON.stringify(data));
        console.log("[LOG] profil info retrieve");
      })
      .catch((error) => {
        console.error("API request error: ", error);
      });
  };

  const getOrgUnits = async () => {
    try {
      const response = await fetch(
        "https://localhost:8041/api/directory/v1/organisations",
        {
          method: "GET",
          headers: headers,
        },
      );
      if (!response.ok) {
        if (response.status === 401) { navigate("/error401"); }
        else if (response.status === 403) { navigate("/error403"); }
        else { throw new Error(response.status + " " + response.statusText); }
      }
      const data = await response.json();
      setOrganizations(data);

      // Flatten the orgUnits array from each organization object
      const flattenedOrgUnits = data.reduce((acc, org) => {
        return acc.concat(org.orgUnits);
      }, []);

      setOrgUnits(flattenedOrgUnits);
    } catch (error) {
      console.error("Error fetching organization units:", error);
    }
  };
  function handleBack() {
    navigate(-1);
  }

  function handleSubmit() {
    // Prepare the data to be sent
    const mappedUserData = {
      lastName: modifiedUser.Lastname || user.Lastname,
      firstName: modifiedUser.Firstname || user.Firstname,
      email: modifiedUser.Email || user.Email,
      phoneNumber: modifiedUser["Phone Number"] || user["Phone Number"],
      job: modifiedUser.Job || user.Job,
      orgUnit: modifiedUser.orgUnit || user["Organization Unit"],
    };

    // API URL for updating user profile
    const apiUrl =
      "https://localhost:8041/api/directory/v1/employees/" + userId;

    console.log(mappedUserData);

    // Perform a PUT request to update the user profile
    fetch(apiUrl, {
      method: "PUT", // Use PUT method for updating
      headers: {
        ...headers,
        "Content-Type": "application/json", // Specify JSON content type
      },
      body: JSON.stringify(mappedUserData), // Convert user object to JSON string
    })
      .then((response) => {
        if (!response.ok) {
          if (response.status === 401) { navigate("/error401"); }
          else if (response.status === 403) { navigate("/error403"); }
          else { throw new Error(response.status + " " + response.statusText); }
        }
        console.log("[LOG] Profile updated successfully");
        alert("Profile updated successfully"); // Show success message
      })
      .catch((error) => {
        console.error("API request error: ", error);
        alert("Failed to update profile"); // Show error message
      });
  }

  const handleChange = (fieldName, value) => {
    setModifiedUser((prevState) => ({
      ...prevState,
      [fieldName]: value,
    }));
    console.log(user["Organization Unit"]);
    console.log(modifiedUser.orgUnit);
  };

  function handleChangePassword() {
    navigate("/updatePassword");
  }

  return (
    <>
      <div className="user-container">
        <div className="title">{title}</div>
        <div className="info-container">
          {Object.entries(user).map(([key, value]) => (
            <div className="input-container" key={key}>
              <label className="label">{key}:</label>
              {key === "Id" ? (
                <input type="text" className="input" disabled value={value} />
              ) : key === "Organization" ? (
                <select
                  className="add-user-input"
                  value={selectedOrg}
                  onChange={(e) => {
                    const orgId = e.target.value;
                    setSelectedOrg(
                      organizations.find((org) => org.id == orgId),
                    );
                  }}
                  defaultValue={user.Organization}
                >
                  {organizations.map((org) => (
                    <option
                      key={org.id}
                      value={org.id}
                      selected={selectedOrg === user.Organization}
                    >
                      {org.name}
                    </option>
                  ))}
                </select>
              ) : key === "Organization Unit" ? (
                <select
                  className="add-user-input"
                  value={modifiedUser.orgUnit || user["Organization Unit"]}
                  onChange={(e) => handleChange("orgUnit", e.target.value)}
                  defaultValue={user["Organization Unit"]}
                >
                  {orgUnits.map((unit) => (
                    <option
                      key={unit.id}
                      value={unit.id}
                      selected={user["Organization Unit"] === unit.id}
                    >
                      {unit.name}
                    </option>
                  ))}
                </select>
              ) : (
                <input
                  type="text"
                  className="input"
                  value={
                    modifiedUser[key] !== undefined ? modifiedUser[key] : value
                  }
                  onChange={(e) => handleChange(key, e.target.value)}
                />
              )}
            </div>
          ))}
        </div>
        <div className="authentification-btn">
          <Button type="back" value="back" onClick={handleBack}>
            Back
          </Button>
          <Button onClick={handleChangePassword}>Modify Password</Button>
          <Button type="submit" value="Submit" onClick={handleSubmit}>
            Submit
          </Button>
        </div>
      </div>
    </>
  );
}

export default ProfilForm;
