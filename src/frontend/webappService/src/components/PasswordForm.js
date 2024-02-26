import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import Button from "./Button";
import "../css/AuthForm.css";

function PasswordChangeForm() {
  const navigate = useNavigate();
  const [errorMessage, setErrorMessage] = useState("");
  const [user, setUser] = useState({
    username: localStorage.getItem("id"),
    newPassword: "",
    showPassword: false,
  });
  // Get token from local storage
  const token = localStorage.getItem("Token");
  // Set headers for the API request
  const headers = {
    Authorization: `Bearer ${token}`,
    "Content-Type": "application/json",
  };

  const handleClickShowPassword = (event) => {
    event.preventDefault();
    setUser({
      ...user,
      showPassword: !user.showPassword,
    });
  };

  const handleMouseDownPassword = (event) => {
    event.preventDefault();
  };

  const handleValueChange = (prop) => (event) => {
    setUser({
      ...user,
      [prop]: event.target.value,
    });
  };

  const handleReset = () => {
    user.newPassword = "";
    setErrorMessage("");
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    // Prepare the data to be sent
    const requestBody = {
      password: user.newPassword
    }

    const apiUrl =
      "https://localhost:8041/api/authentication/v1/login-profiles/" +
      user.username +
      "/password";

    fetch(apiUrl, {
      method: "PATCH",
      headers: headers,
      body: JSON.stringify(requestBody),
    })
      .then((response) => {
        if (!response.ok) throw new Error(response.status);
        console.log("[LOG] Password updated successfully");
        navigate("/home");
      })
      .catch((error) => {
        setErrorMessage("Failed to update password");
        console.error("API request error: ", error);
      });
  };

  return (
    <>
      <div className="authentification-container">
        <div className="title">Modify Password</div>
        <form>
          <div className="usernameInput">
            <label>Username :</label>
            <input
              type="text"
              className="input"
              disabled
              value={user.username}
            />
          </div>

          <div className="passwordInput">
            <label>New Password :</label>
            <input
              type={user.showPassword ? "text" : "password"}
              name="newPassword"
              onChange={handleValueChange("newPassword")}
              value={user.newPassword}
            />
            <Button
              onClick={handleClickShowPassword}
              onMouseDown={handleMouseDownPassword}
            >
              {user.showPassword ? "O" : "X"}
            </Button>
            {errorMessage && <div className="error">{errorMessage}</div>}
          </div>

          <div className="authentification-btn">
            <Button type="reset" value="Reset" onClick={handleReset}>
              Reset
            </Button>
            <Button type="submit" value="Submit" onClick={handleSubmit}>
              Submit
            </Button>
          </div>
        </form>
      </div>
    </>
  );
}

export default PasswordChangeForm;
