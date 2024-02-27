import React, { useEffect, useState } from "react";
import "../css/AddUser.css";

function AddUserForm({ title }) {
  const [roles, setRoles] = useState({});
  const [selectedRoles, setSelectedRoles] = useState([]);
  const [userEmail, setUserEmail] = useState("");

  useEffect(() => {
    fetchRoles();
  }, []);

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
        throw new Error(response.status);
      }
      const data = await response.json();
      setRoles(data);
    } catch (error) {
      console.error("API request error: ", error);
    }
  };

  const handleEmailChange = (e) => {
    setUserEmail(e.target.value);
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

  const handleSubmit = async () => {
    try {
      const response = await fetch(
        "https://localhost:8041/api/authentication/v1/login-profiles",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${localStorage.getItem("Token")}`,
          },
          body: JSON.stringify({ email: userEmail, roles: selectedRoles }),
        },
      );

      if (!response.ok) {
        throw new Error("Failed to submit user email");
      }

      // Handle success response
      console.log("User email submitted successfully");
    } catch (error) {
      console.error("Error submitting user email:", error);
    }
  };
  return (
    <>
      <div className="user-form-container">
        <div className="title">{title}</div>
        <form className="add-user-form" onSubmit={handleSubmit}>
          <div className="email-input-container">
            <label className="add-user-label">Email</label>
            <input
              className="add-user-input"
              type="text"
              id="email"
              value={userEmail}
              onChange={handleEmailChange}
            />
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
      </div>
    </>
  );
}

export default AddUserForm;
