import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "../css/ProfilForm.css";
import Button from "./Button";

function ProfilForm({ title, userId }) {
  const navigate = useNavigate();
  const [user, setUser] = useState({});
  const [modifiedUser, setModifiedUser] = useState({});
  const [orgUnits, setOrgUnits] = useState([]);

  const tokenName = "Token"; // Need to be the same name as in AuthForm.js components
  const token = localStorage.getItem(tokenName);

  // Prepare the 'Authorization' header with the value 'Bearer' and the token
  const headers = {
    Authorization: `Bearer ${token}`,
  };

  // Get the user profil information
  useEffect(() => {
    getUserDir();
    getUserAuth();
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
        if (!response.ok) throw new Error(response.status);
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
          "Organization Unit": data.orgUnit?.name,
          Organization: data.organisation?.name,
        }));
        console.info("[DATA] " + JSON.stringify(data));
        console.log("[LOG] profil info retrieve");
      })
      .catch((error) => {
        console.error("API request error: ", error);
      });
  };

  // Get user from API Authentification
  const getUserAuth = async () => {
    // API URL
    const apiUrl =
      "https://localhost:8041/api/authentication/v1/login-profiles/" + userId;

    await fetch(apiUrl, {
      method: "GET",
      headers: headers,
    })
      .then((response) => {
        if (!response.ok) throw new Error(response.status);
        const res = response.json();
        return res;
      })
      .then((data) => {
        const roleNames = data.roles.map((role) => role.name);
        setUser((prevUser) => ({
          ...prevUser,
          Roles: roleNames,
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
        throw new Error("Failed to fetch organization units");
      }
      const data = await response.json();

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
        if (!response.ok) throw new Error(response.status);
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
  };

  return (
    <>
      <div className="user-container">
        <div className="title">{title}</div>
        <div className="info-container">
          {Object.entries(user).map(([key, value]) => (
            <div className="input-container" key={key}>
              <label className="label">{key}:</label>
              {key === "Id" || key === "Organization" ? (
                <input type="text" className="input" disabled value={value} />
              ) : key === "Roles" ? (
                <input
                  type="text"
                  className="input"
                  disabled
                  value={value.sort().join(" ; ").toUpperCase()}
                />
              ) : key === "Organization Unit" ? (
                <select
                  className="input"
                  value={modifiedUser.orgUnit || user.orgUnit}
                  onChange={(e) =>
                    handleChange("orgUnit", parseInt(e.target.value))
                  }
                >
                  <option value="">Select Organization Unit</option>
                  {orgUnits.map((unit) => (
                    <option key={unit.id} value={unit.id}>
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
          <Button type="submit" value="Submit" onClick={handleSubmit}>
            Submit
          </Button>
        </div>
      </div>
    </>
  );
}

export default ProfilForm;
