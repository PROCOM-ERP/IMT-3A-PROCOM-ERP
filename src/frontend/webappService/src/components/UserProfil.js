import React, { useState, useEffect } from "react";
import Button from "../components/Button.js"
import "../css/App.css";
import "../css/UserProfil.css";
import { useNavigate } from 'react-router-dom';

function UserProfil({ title, userId }) {
  const [user, setUser] = useState({});
  const navigate = useNavigate();

  const tokenName = "Token"; // Need to be the same name as in AuthForm.js components
  const token = localStorage.getItem(tokenName);

  // Prepare the 'Authorization' header with the value 'Bearer' and the token
  const headers = {
    'Authorization': `Bearer ${token}`,
  };

  // Get the user profil information
  useEffect(() => {
    getUserDir();
    getUserAuth();
  }, []);

  // Get user from API Directory
  const getUserDir = async () => {
    // API URL
    const apiUrl = "https://localhost:8041/api/dir/v1/employees/" + userId;

    await fetch(apiUrl, {
      method: "GET",
      headers: headers,
    })
      .then((response) => {
        if (!response.ok) throw new Error(response.status);
        const res = response.json();
        return res;
      })
      .then(data => {
        setUser(prevUser => ({
          ...prevUser,
          Id: data.id,
          Lastname: data.lastName,
          Firstname: data.firstName,
          Email: data.email,
          "Phone Number": data.phoneNumber
        }));
        console.info("[DATA] " + JSON.stringify(data));
        console.log("[LOG] profil info retrieve");
      })
      .catch(error => {
        console.error('API request error: ', error);
      });
  }

  // Get user from API Authentification
  const getUserAuth = async () => {
    // API URL
    const apiUrl = "https://localhost:8041/api/auth/v1/employees/" + userId;

    await fetch(apiUrl, {
      method: "GET",
      headers: headers,
    })
      .then((response) => {
        if (!response.ok) throw new Error(response.status);
        const res = response.json();
        return res;
      })
      .then(data => {
        setUser(prevUser => ({
          ...prevUser,
          Roles: data.roles
        }));
        console.info("[DATA] " + JSON.stringify(data));
        console.log("[LOG] profil info retrieve");
      })
      .catch(error => {
        console.error('API request error: ', error);
      });
  }

  // Modify the user profil TODO
  function handleModif(event) {
    // Prevent the browser from reloading the page
    event.preventDefault();
    navigate("/modifyProfil"); // Navigate to the page
  }

  const renderPasswordButton = () => {
    if (title.toLowerCase() === "profil") {
      return (<Button type="button" value="modify" onClick={handleModif}>Modify</Button>);
    }
  }

  return (
    <>
      <div className="user-container">
        <div className="title">{title}</div>
        {Object.entries(user).map(([key, value]) => (
          <div className="info-container">
            <strong>{key}:</strong>
            <span>{Array.isArray(value) ? value.join(', ') : value}</span>
          </div>
        ))}
        {renderPasswordButton()}
      </div>
    </>
  );
}

export default UserProfil;
