import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom';
import Button from './Button';
import '../css/RoleForm.css';

function RoleForm() {
  const [roleData, setRoleData] = useState({ name: '', microservices: [] });
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setRoleData(prevData => ({
      ...prevData,
      [name]: value
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    // TODO : verify input

    const token = localStorage.getItem('Token');
    const headers = {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    };
    const apiUrl = "https://localhost:8041/api/authentication/v1/roles";

    // You can make an API request here to send roleData to the server
    fetch(apiUrl, {
      method: 'POST',
      headers: headers,
      body: JSON.stringify(roleData)
    })
      .then(response => {
        if (!response.ok) {
          throw new Error('Failed to add role');
        }
        console.log("[LOG] Role added with success");
        navigate("/adminPermissions");
      })
      .catch(error => {
        console.error('Error adding role:', error);
      });
  };

  return (
    <>
      <div className='add-role-container'>
        <div className='title'>Create New Role</div>
        <div className='role-form-container'>
          <form onSubmit={handleSubmit}>
            <div className='input-container'>
              <label htmlFor="name">Name</label>
              <input type="text" id="name" name="name" value={roleData.name} onChange={handleChange} />
            </div>
            <div className='create-role-button'>
              <Button type="submit">Create Role</Button>
            </div>
          </form>
        </div>
      </div>
    </>
  );
}

export default RoleForm
