import React, { useState, useEffect } from "react";
import Button from "../components/Button.js";
import "../css/App.css";
import "../css/UserProfil.css";
import { useNavigate } from "react-router-dom";

function UserProfilAdmin({ title, userId }) {
  const navigate = useNavigate();
  const [userRoles, setUserRoles] = useState({});
  const [userInfo, setUserInfo] = useState({});
  const [modifiedUserRoles, setModifiedUserRoles] = useState({});
  const [modifiedUserInfo, setModifiedUserInfo] = useState({});
  const [modify, setModify] = useState(false);

  const tokenName = "Token"; // Need to be the same name as in AuthForm.js components
  const token = localStorage.getItem(tokenName);

  // Prepare the 'Authorization' header with the value 'Bearer' and the token
  const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  };

  // Get the user profil information
  useEffect(() => {
    getUserDir();
    getUserAuth();
  }, []);

  // Get user from API Directory
  const getUserDir = async () => {
    // API URL
    const apiUrl = "https://localhost:8041/api/directory/v1/employees/" + userId;

    await fetch(apiUrl, {
      method: "GET",
      headers: headers,
    })
      .then((response) => {
        if (!response.ok) {
          if (response.status === 401) { navigate("/error401"); }
          else if (response.status === 403) { navigate("/error403"); }
          else { throw new Error(response.status + " " + response.statusText); }
        }
        const res = response.json();
        return res;
      })
      .then((data) => {
        setUserInfo((prevUser) => ({
          ...prevUser,
          Id: data.id,
          Lastname: data.lastName,
          Firstname: data.firstName,
          Email: data.email,
          "Phone Number": data.phoneNumber,
          Job: data.job,
          Organisation: data.organisation.name,
          Unit: data.orgUnit.name,
          City: data.orgUnit.city,
          Country: data.orgUnit.country,
        }));
        setModifiedUserInfo((prevUser) => ({
          ...prevUser,
          Lastname: data.lastName,
          Firstname: data.firstName,
          Email: data.email,
          "Phone Number": data.phoneNumber,
          Job: data.job,
          OrgUnitId: data.orgUnit.id,
        }));
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
        if (!response.ok) {
          if (response.status === 401) { navigate("/error401"); }
          else if (response.status === 403) { navigate("/error403"); }
          else { throw new Error(response.status + " " + response.statusText); }
        }
        const res = response.json();
        return res;
      })
      .then((data) => {
        setUserRoles((prevUser) => ({
          ...prevUser,
          isEnabled: data.isEnable,
          Roles: data.roles,
        }));
        setModifiedUserRoles((prevUser) => ({
          ...prevUser,
          isEnabled: data.isEnable,
          Roles: data.roles,
        }));

        console.log("[LOG] profil info retrieve");
      })
      .catch((error) => {
        console.error("API request error: ", error);
      });
  };


  const handleChangeInfo = (e) => {
    const { name, value } = e.target;
    setModifiedUserInfo(prevUser => ({
      ...prevUser,
      [name]: value
    }));
  };

  // Modify the user profil
  function handleModif(event) {
    // Prevent the browser from reloading the page
    event.preventDefault();
    setModify(!modify);
    if (modify == true) {
      if (JSON.stringify(modifiedUserInfo) !== JSON.stringify(userInfo)) {
        console.log("[LOG] There are some modification in userInfo!");
        updateUserInfo();
        setTimeout(() => {
          // Code to execute after waiting for 10 milliseconds
          getUserDir();
        }, 30);
      }

      if (JSON.stringify(modifiedUserRoles) !== JSON.stringify(userRoles)) {
        console.log("[LOG] There are some modification in userRoles!");
        updateUserRoles();
        setTimeout(() => {
          // Code to execute after waiting for 10 milliseconds
          getUserAuth();
        }, 30);
      }


    }
  }

  function handleBack(event) {
    // Prevent the browser from reloading the page
    event.preventDefault();
    setModify(!modify);
    setModifiedUserInfo(prevUser => ({
      ...prevUser,
      Lastname: userInfo.lastName,
      Firstname: userInfo.firstName,
      Email: userInfo.email,
      "Phone Number": userInfo.phoneNumber,
      Job: userInfo.job,
    }));
    setModifiedUserRoles(userRoles);
  }

  function updateUserInfo() {

    const { Lastname, Firstname, Email, "Phone Number": phoneNumber, Job, OrgUnitId } = modifiedUserInfo;
    const dataToSend = {
      lastName: Lastname,
      firstName: Firstname,
      email: Email,
      phoneNumber: phoneNumber,
      job: Job,
      orgUnit: OrgUnitId // Assuming orgUnit is an integer value
    };

    const apiUrl = "https://localhost:8041/api/directory/v1/employees/" + userId;

    // Send API request to update database
    fetch(apiUrl, {
      method: 'PUT',
      headers: headers,
      body: JSON.stringify(dataToSend)
    })
      .then(response => {
        if (!response.ok) {
          if (response.status === 401) { navigate("/error401"); }
          else if (response.status === 403) { navigate("/error403"); }
          else { throw new Error(response.status + " " + response.statusText); }
        }
        console.log("[LOG] User information updated with success");
      })
      .catch(error => {
        console.error('Error saving changes for user information:', error);
      });
  }

  function updateUserRoles() {
    // Assuming userRoles is already defined
    const { isEnabled, Roles } = modifiedUserRoles;

    // Filter roles where isEnable is true and extract role names
    const enabledRoles = Roles.filter(role => role.isEnable).map(role => role.name);

    // Create dataToSend object with the correct format
    const dataToSend = {
      isEnable: isEnabled,
      roles: enabledRoles
    };

    console.log("data to send: ");
    console.log(JSON.stringify(dataToSend));

    const apiUrl = "https://localhost:8041/api/authentication/v1/login-profiles/" + userId;

    // Send API request to update database
    fetch(apiUrl, {
      method: 'PUT',
      headers: headers,
      body: JSON.stringify(dataToSend)
    })
      .then(response => {
        if (!response.ok) {
          if (response.status === 401) { navigate("/error401"); }
          else if (response.status === 403) { navigate("/error403"); }
          else { throw new Error(response.status + " " + response.statusText); }
        }
        console.log("[LOG] User roles updated with success");
      })
      .catch(error => {
        console.error('Error saving changes for user roles:', error);
      });
  }

  const handleIsEnabledChange = (e) => {
    const checked = e.target.checked;
    console.log(checked);
    setModifiedUserRoles(prevUser => ({
      ...prevUser,
      isEnabled: checked
    }));
    console.log(modifiedUserRoles);
  };

  return (
    <>
      <div className="user-form-container">
        <div className="title">{title}</div>
        <div className="information-container">
          {Object.entries(userInfo).map(([key, value]) => (
            <div className="information">
              <label htmlFor={key} >{key}
                <input
                  name={key}
                  value={modify ? modifiedUserInfo[key] : value}
                  // disable all input fields except those present in modifiedUserInfo when modify is true
                  disabled={!modify || !(key in modifiedUserInfo)}
                  onChange={handleChangeInfo}
                />
              </label>

            </div>
          ))}
          <div className="information-container">
            <label>
              <input
                type="checkbox"
                name={"isEnabled"}
                disabled={!modify}
                checked={modify ? modifiedUserRoles.isEnabled : userRoles.isEnabled}
                onChange={handleIsEnabledChange}
              />
              Is active
            </label>
            <label>Roles:</label>
            {userRoles.Roles && (userRoles.Roles.map((role, index) => (

              <div key={index}>
                <label>
                  <input
                    type="checkbox"
                    disabled={!modify}
                    name={`role-${index}`}
                    checked={modify ? modifiedUserRoles.Roles[index].isEnable : role.isEnable}
                    onChange={(e) => {
                      const checked = e.target.checked;
                      setModifiedUserRoles(prevUser => ({
                        ...prevUser,
                        Roles: [
                          ...prevUser.Roles.slice(0, index),
                          { ...prevUser.Roles[index], isEnable: checked },
                          ...prevUser.Roles.slice(index + 1)
                        ]
                      }));
                    }}
                  />
                  {role.name}
                </label>
              </div>
            )))}
          </div>
        </div>
        <div className="button-container">
          <Button onClick={handleModif}> {modify ? "Save" : "Modify"} </Button>
          {(modify && (
            <Button onClick={handleBack}>Back</Button>
          ))}
        </div>
      </div>
    </>
  );
}

export default UserProfilAdmin;
