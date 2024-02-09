import React, { useState, useEffect } from "react";
import Button from "../components/Button.js"
import "../css/App.css";
import "../css/UserProfil.css";

function UserProfil({ title, userId }) {

  const [user, setUser] = useState({});

  const tokenName = "Token"; // Need to be the same name as in AuthForm.js components
  const token = localStorage.getItem(tokenName);

  // Prepare the 'Authorization' header with the value 'Bearer' and the token
  const headers = {
    'Authorization': `Bearer ${token}`,
  };

  // Get the user profil information
  useEffect(() => {
    getUser();
  }, []);

  const getUser = async () => {
    // API URL
    const apiUrl = "https://localhost:8041/api/dir/v1/employees/" + userId; // TODO: to change to auth {"id":"A00001","email":null,"roles":["admin","employee"]}

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
        setUser(data);
        console.info("[DATA] " + JSON.stringify(data));
        console.log("[LOG] profil info retrieve");
      })
      .catch(error => {
        console.error('API request error: ', error);
      });
  }

  // Modify the user profil
  function handleModif(event) {
    // Prevent the browser from reloading the page
    event.preventDefault();

    // API URL
    const apiUrl = "https://localhost:8041/api/dir/v1/employees";

    // Make the API request
    fetch(apiUrl, {
      method: "GET",
      headers: headers,
    })
      .then((response) => {
        if (!response.ok) throw new Error(response.status);
        const res = response.json();
        return res;
      })
      .then(data => {
        setUser(data);
        console.log("[LOG] profil info retrieve");
      })
      .catch(error => {
        console.error('API request error: ', error);
      });
  }

  const renderPasswordButton = () => {
    if (title.toLowerCase() === "profil") {
      return (<Button type="button" value="modify" onClick={handleModif}>Modify password</Button>);
    }
  }

  return (
    <>
      <div className="user-container">
        <div className="title">{title}</div>
        <p>Firstname : {user.firstName}</p>
        <p>Lastname : {user.lastName}</p>
        <p>Email : {user.email}</p>
        <p>Phone Number : {user.phoneNumber}</p>
        {renderPasswordButton()}
      </div>
    </>
  );
}

export default UserProfil;
