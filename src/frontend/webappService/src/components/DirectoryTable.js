import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import "../css/DirectoryTable.css";
import Button from "./Button";

function DirectoryTable({ isAdmin = false }) {
  //const defaultUsers = data.users;
  const navigate = useNavigate();
  const userId = localStorage.getItem("id");
  const [users, setUsers] = useState([]);

  const [searchTerm, setSearchTerm] = useState("");
  const [sortBy, setSortBy] = useState(null);

  const handleChange = (e) => {
    setSearchTerm(e.target.value);
  };

  const handleSort = (key) => {
    setSortBy(key);
  };

  const handleProfil = (id) => {
    isAdmin ? navigate("/manageUser/" + id) : navigate("/user/" + id);
  };

  let filteredUsers = users.filter(user => {
    const lowerCaseSearchTerm = searchTerm.toLowerCase();
    return (
      (user.id && user.id.toLowerCase().includes(lowerCaseSearchTerm)) ||
      (user.firstName && user.firstName.toLowerCase().includes(lowerCaseSearchTerm)) ||
      (user.lastName && user.lastName.toLowerCase().includes(lowerCaseSearchTerm)) ||
      (user.email && user.email.toLowerCase().includes(lowerCaseSearchTerm)) ||
      (user.job && user.job.toLowerCase().includes(lowerCaseSearchTerm)) ||
      (user.organisation.name && user.organisation.name.toLowerCase().includes(lowerCaseSearchTerm)) ||
      (user.orgUnit.address.city && user.orgUnit.address.city.toLowerCase().includes(lowerCaseSearchTerm)) ||
      (user.phoneNumber && user.phoneNumber.toLowerCase().includes(lowerCaseSearchTerm))
    );
  });

  if (sortBy) {
    filteredUsers.sort((a, b) => {
      if (a[sortBy] < b[sortBy]) return -1;
      if (a[sortBy] > b[sortBy]) return 1;
      return 0;
    });
  }

  const tokenName = "Token"; // Need to be the same name as in AuthForm.js components
  const token = localStorage.getItem(tokenName);
  const apiUrl = "https://localhost:8041/api/directory/v1/employees";

  const headers = {
    Authorization: `Bearer ${token}`,
  };

  const getEmployees = async () => {
    // Make the API request
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
        setUsers(data);
        console.log("[LOG] Users profil information retrieved");
      })
      .catch((error) => {
        console.error("API request error: ", error);
        if (error.status === 401) navigate("/error401");
        if (error.status === 403) navigate("/error403");
      });
  };

  useEffect(() => {
    getEmployees();
  }, []);

  useEffect(() => {
    getEmployees();
  }, []);

  function handleAddUser() {
    navigate("/addUser");
  }

  return (
    <>
      <div className='directory-container'>
        <div className="line-container">
          <div className="searchbar-container">
            <input className='searchbar'
              type="text"
              placeholder="Search"
              value={searchTerm}
              onChange={handleChange}
            />
          </div>
          {isAdmin && (<Button className="add-user-button" onClick={handleAddUser}>Add User</Button>)}
        </div>
        <table className='table-container' >
          <thead className='table-head-container'>
            <tr>
              <th onClick={() => handleSort('id')} >ID</th>
              <th onClick={() => handleSort('firstName')} >Firstname</th>
              <th onClick={() => handleSort('lastName')} >Lastname</th>
              <th onClick={() => handleSort('email')} >Email</th>
              <th onClick={() => handleSort('job')} >Job</th>
              <th onClick={() => handleSort('organisation')} >Organisation</th>
              <th onClick={() => handleSort('city')} >City</th>
              <th onClick={() => handleSort('phoneNumber')} >Phone number</th>
            </tr>
          </thead>
          <tbody className='table-body-container'>
            {filteredUsers.map((user, index) => {
              return (
                <tr key={index} onClick={() => handleProfil(user.id)} >
                  <td>{user.id}</td>
                  <td> {user.firstName} </td>
                  <td> {user.lastName} </td>
                  <td> {user.email} </td>
                  <td> {user.job} </td>
                  <td> {user.organisation.name} </td>
                  <td> {user.orgUnit.address.city} </td>
                  <td> {user.phoneNumber} </td>
                </tr>
              )
            })}

          </tbody>
        </table>
      </div>
    </>
  )
}

export default DirectoryTable;
