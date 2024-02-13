import React, { useState, useEffect } from "react";
import Form from "./AuthForm";


function ProfilForm({ title, userId }) {
  const [user, setUser] = useState({});

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

  return (
    <>
      <p>Petit texte avant le formulaire</p>
      <form method="get" action="">
        <p>
          <label for="nom">nom</label> : <input type="text" name="nom" id="nom" placeholder="test"/>
        </p>
      </form>
      <p>texte après le formulaire</p>
    </>




    /*<>
         {Object.entries(user).map(([key, value]) => (
           <div className="info-container">
             <form>{key}:</form>
             <input type="text"></input>
           </div>
         ))}
     </>*/
  )
}

export default ProfilForm
