import React, { useState, useEffect } from "react";
import Button from "./Button";

function ProfilForm({ title, userId }) {
  const [user, setUser] = useState({});

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
      "https://localhost:8041/api/authentication/v1/employees/" + userId;

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
          Roles: data.roles,
        }));
        console.info("[DATA] " + JSON.stringify(data));
        console.log("[LOG] profil info retrieve");
      })
      .catch((error) => {
        console.error("API request error: ", error);
      });
  };

  function handleBack() {
    alert("test");
  }

  function handleSubmit() {
    alert("test");
  }

  const handleChange = (fieldName, value) => {
    setUser((prevState) => ({
      ...prevState,
      [fieldName]: value,
    }));
  };

  return (
    <>
      {Object.entries(user).map(([key, value]) => (
        <div className="info-container">
          <label>{key}:</label>
          <input
            type="text"
            value={value}
            readOnly={key === "Roles" || key === "Id"} // Only set readOnly for 'role' and 'Id'
            onChange={
              key !== "Roles" && key !== "Id"
                ? (e) => handleChange(key, e.target.value)
                : undefined
            } // Add a handler for changes on other fields
          />
        </div>
      ))}
      <div className="authentification-btn">
        <Button type="back" value="back" onClick={handleBack}>
          Back
        </Button>
        <Button type="submit" value="Submit" onClick={handleSubmit}>
          Submit
        </Button>
      </div>
    </>
  );
}

export default ProfilForm;
