import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Button from '../Button';
import '../../css/RoleForm.css';
import handleFormError from "../../utils/handleFormError";
import ErrorForm from "../../pages/errors/ErrorForm";

function RoleForm() {
  // State to hold the role data
  const [roleData, setRoleData] = useState({ name: '', microservices: [] });
  // Hook to navigate to another page
  const navigate = useNavigate();

  const [error, setError] = useState({ title: null, message: null });
  const [gettingError, setGettingError] = useState(false);

  // Function to handle input change
  const handleChange = (e) => {
    const { name, value } = e.target;
    // Update the role data state
    setRoleData(prevData => ({
      ...prevData,
      [name]: value
    }));
  };

  // Function to handle form submission
  const handleSubmit = (e) => {
    e.preventDefault();
    // TODO: Add input validation

    // Get token from local storage
    const token = localStorage.getItem('Token');
    // Set headers for the API request
    const headers = {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    };
    // API URL for adding a new role
    const apiUrl = "https://localhost:8041/api/authentication/v1/roles";

    // Make an API request to add the new role
    fetch(apiUrl, {
      method: 'POST',
      headers: headers,
      body: JSON.stringify(roleData)
    })
      .then(async (response) => {
        const [getError, error] = await handleFormError(response, navigate);
        if (getError) {
          setGettingError(true);
          setError(error);
        } else {
          console.log("[LOG] Role added with success");
          // Navigate to the admin permissions page after successfully adding the role
          navigate("/adminPermissions");
        }
      })
      .catch(error => {
        console.error('Error adding role:', error);
      });
  };

  return (
    <>
      <div className='add-role-container'>
        {/* Title for the role form */}
        <div className='title'>Create New Role</div>
        <div className='role-form-container'>
          <form onSubmit={handleSubmit}>
            {/* Input field for entering role name */}
            <div className='input-container'>
              <label htmlFor="name">Name</label>
              <input type="text" id="name" name="name" value={roleData.name} onChange={handleChange} />
            </div>
            {/* Button to submit the form and create the role */}
            <div className='create-role-button'>
              <Button type="submit">Create Role</Button>
            </div>
          </form>
        </div>
      </div>
      {gettingError && (
        <ErrorForm
          title={error.title}
          message={error.message}
          onClose={() => {
            setGettingError(false);
          }}
        />
      )}
    </>
  );
}

export default RoleForm;
