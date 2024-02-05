import React, { useState, useEffect } from "react";
import Navbar from "../components/Navbar";
import Button from "../components/Button.js"
import "../css/App.css";
import "../css/Profil.css";

function Profil() {

  useEffect(() => {
    // Define the API URL
    const apiUrl = "https://localhost:8041/api/dir/v1/hello";

    // Make the API request
    fetch(apiUrl, {
      method: "GET",
      credentials: "include",
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Network response was not ok");
        }
        return response.text(); // Parse the response as text
      })
      .then((data) => {
        console.log("API Response:", data); // Check the response in the console
        //setMessage(data); // Set the response as the message
      })
      .catch((error) => {
        console.error("API request error:", error);
      });
  }, []);

  const user = {
    id: 1,
    firstname: "Aina",
    lastname: "Dirou",
    age: 23,
    job: "Frontend Developper",
    office: "Brest",
    department: "Software Engineering",
  };

  function handleModif() {
    console.log("handle modification TODO");
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
