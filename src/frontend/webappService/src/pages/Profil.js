import React, { useState, useEffect } from "react";
import Navbar from "../components/Navbar";
import Button from "../components/Button.js"
import "../css/App.css";
import "../css/Profil.css";

function Profil() {

  const [user, setUser] = useState({
    firstname : "Aina",
    name: "Dirou",
    age: 23,
    office: "Brest",
    department: "Software",
    job: "Software Development"
  });
  const tokenName = "Token"; // Need to be the same name as in AuthForm.js components
  const token = localStorage.getItem(tokenName);

  // Prepare the 'Authorization' header with the value 'Bearer' and the token
  const headers = {
    'Authorization': `Bearer ${token}`,
  };

  // Get the user profil information
  useEffect(() => {
    // API URL
    const apiUrl = "https://localhost:8041/api/dir/v1/employees"; // TODO: to change

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
          //setUser(data);
          console.info("[DATA] " + data);
          console.log("[LOG] profil info retrieve");
      })
      .catch(error => {
          console.error('API request error: ', error);
      });

  });

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

  return (
    <>
      <Navbar />
      <div className="profil-container">
        <div className="title">Profil</div>
        <p>Firstname : {user.firstname}</p>
        <p>Lastname : {user.lastname}</p>
        <p>Age : {user.age}</p>
        <p>Office : {user.office}</p>
        <p>Department : {user.department}</p>
        <p>Job : {user.job}</p>
        <Button type="button" value="modify" onClick={handleModif}>Modify</Button>
      </div>
    </>
  );
}

export default Profil;
