import React, { useState, useEffect } from "react";
import Button from "../components/Button.js";
import "../css/App.css";
import "../css/UserProfil.css";
import { useNavigate } from "react-router-dom";

function UserProfil({ title, userId }) {
  const [user, setUser] = useState({});
  const navigate = useNavigate();
  const [maxKeyWidth, setMaxKeyWidth] = useState(0);
  const [maxValueWidth, setMaxValueWidth] = useState(0);

  const tokenName = "Token"; // Need to be the same name as in AuthForm.js components
  const token = localStorage.getItem(tokenName);

  // Prepare the 'Authorization' header with the value 'Bearer' and the token
  const headers = {
    Authorization: `Bearer ${token}`,
  };

  // Get the user profil information
  useEffect(() => {
    getUserDir();
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
          if (response.status === 401) {
            navigate("/error401");
          } else if (response.status === 403) {
            navigate("/error403");
          } else {
            throw new Error(response.status + " " + response.statusText);
          }
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

  function handleModif(event) {
    // Prevent the browser from reloading the page
    event.preventDefault();
    navigate("/modifyProfil"); // Navigate to the page
  }

  // Calculate the maximum key width
  useEffect(() => {
    const entries = Object.entries(user);
    let maxKeyWidth = 0;
    let maxValueWidth = 0;

    entries.forEach(([key, value]) => {
      const keyWidth = getTextWidth(key); // Function to calculate text width for key
      const valueWidth = getTextWidth(value); // Function to calculate text width for value
      maxKeyWidth = Math.max(maxKeyWidth, keyWidth);
      maxValueWidth = Math.max(maxValueWidth, valueWidth);
    });

    setMaxKeyWidth(maxKeyWidth);
    setMaxValueWidth(maxValueWidth);
  }, [user]);

  const getTextWidth = (text) => {
    const container = document.createElement("div"); // Create a temporary container
    container.style.visibility = "hidden"; // Hide the container
    container.style.whiteSpace = "nowrap"; // Prevent text wrapping
    container.style.position = "absolute"; // Position off-screen
    container.textContent = text; // Set the text content

    document.body.appendChild(container); // Append container to the document body
    const width = container.offsetWidth; // Get the width of the container
    document.body.removeChild(container); // Remove container from the document body

    return width; // Return the width
  };

  const renderPasswordButton = () => {
    if (title.toLowerCase() === "profil") {
      return (
        <Button type="button" value="modify" onClick={handleModif}>
          Modify
        </Button>
      );
    }
  };

  return (
    <>
      <div className="user-profile-container">
        <div className="title">{title}</div>
        <div className="information-container">
          {Object.entries(user).map(([key, value]) => (
            <div className="information">
              <div
                className="key-container"
                style={{ width: `${maxKeyWidth}px` }}
              >
                {key}
              </div>
              <div
                className="value-container"
                style={{ width: `${maxValueWidth}px` }}
              >
                <span>{Array.isArray(value) ? value.join(", ") : value}</span>
              </div>
            </div>
          ))}
        </div>
        <div className="modify-btn">{renderPasswordButton()}</div>
      </div>
    </>
  );
}

export default UserProfil;
