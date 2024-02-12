import React, { useEffect, useState } from 'react'

function DisplayPermissions() {
  const [services, setServices] = useState({ 0: "Authentification", 1: "Directory", 2: "Inventary" });
  const [roles, setRoles] = useState({ 0: "Admin", 1: "Employee" });
  const [selectedService, setSelectedService] = useState('');
  const [selectedRole, setSelectedRole] = useState('');
  const [permissions, setPermissions] = useState({});

  const [isSelected, setIsSelected] = useState(false); // to set true if a service AND a role is selected

  const user = localStorage.getItem('id');
  const token = localStorage.getItem('Token');

  const headers = { 'Authorization': `Bearer ${token}` };


  // TODO
  // const getServicesAndRoles = async () => {
  //   const apiUrl = "https://localhost:8041/api/auth/v1/auth/jwt"; // TODO: to change
  //   // Make the API request
  //   await fetch(apiUrl, {
  //     method: "GET",
  //     headers: headers,
  //   })
  //     .then((response) => {
  //       if (!response.ok) throw new Error(response.status);
  //       const res = response.json();
  //       return res;
  //     })
  //     .then(data => {
  //       setServices(data.services);
  //       setRoles(data.roles)
  //       console.log("[LOG] List of services and roles retrieved");
  //     })
  //     .catch(error => {
  //       console.error('API request error: ', error);
  //     });
  // }

  // TODO
  // const getPermissions = async () => {
  //   const apiUrl = "https://localhost:8041/api/auth/v1/auth/jwt"; // TODO: to change
  //   // Make the API request
  //   await fetch(apiUrl, {
  //     method: "GET",
  //     headers: headers,
  //   })
  //     .then((response) => {
  //       if (!response.ok) throw new Error(response.status);
  //       const res = response.json();
  //       return res;
  //     })
  //     .then(data => {
  //       setPermissions(data);
  //       console.log("[LOG] Permissions retrieved");
  //     })
  //     .catch(error => {
  //       console.error('API request error: ', error);
  //     });
  // }

  const handleServiceChange = (e) => {
    setSelectedService(e.target.value);
  };

  const handleRoleChange = (e) => {
    setSelectedRole(e.target.value);
  };

  const handleSelect = () => {
    if (selectedService && selectedRole) {
      setIsSelected(true);
    }
  }

  useEffect(() => {
    // getServicesAndRoles();
    handleSelect();
    // if (isSelected) {
    //   getPermissions();
    // }
  }, []);

  return (
    <>
      <h1>Select Services and Roles</h1>
      <div>
        <label htmlFor="services">Select Service:</label>
        <select id="services" value={selectedService} onChange={handleServiceChange}>
          <option value="">Select a service</option>
          {Object.entries(services).map((key, value) => (
            <option key={key} >{value}</option>
          ))}
        </select>
      </div>
      <div>
        <label htmlFor="roles">Select Role:</label>
        <select id="roles" value={selectedRole} onChange={handleRoleChange}>
          <option value="">Select a role</option>
          {Object.entries(roles).map((key, value) => (
            <option key={key} > {value} </option>
          ))}
        </select>
      </div>
      <div>
        <p>Selected Service: {selectedService}</p>
        <p>Selected Role: {selectedRole}</p>
      </div>

    </>
  )
}

export default DisplayPermissions
