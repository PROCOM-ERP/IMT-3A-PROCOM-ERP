import React, { useEffect, useState } from 'react'

function DisplayPermissions() {
  const [services, setServices] = useState({ 0: "Authentification", 1: "Directory", 2: "Inventary" });
  const [roles, setRoles] = useState({ 0: "Administrator", 1: "Employee" });
  const [selectedService, setSelectedService] = useState('');
  const [selectedRole, setSelectedRole] = useState('');
  const [isEnabled, setIsEnabled] = useState();
  const [permissions, setPermissions] = useState({});

  const [areSelected, setAreSelected] = useState(false); // to set true if a service AND a role is selected

  const user = localStorage.getItem('id');
  const token = localStorage.getItem('Token');

  const headers = { 'Authorization': `Bearer ${token}` };

  useEffect(() => {
    // Simulate fetching services and roles from an API
    // Replace with actual API calls
    // fetchServicesAndRoles();
    setServices({ 0: "Authentification", 1: "Directory", 2: "Inventory" });
    setRoles({ 0: "Administrator", 1: "Employee" });
  }, []);

  useEffect(() => {
    if (selectedService && selectedRole) {
      setAreSelected(true);
      // Fetch permissions based on selected service and role
      fetchPermissions(selectedService, selectedRole);
    } else {
      setAreSelected(false);
      setPermissions({});
    }
  }, [selectedService, selectedRole]);

  // TODO
  // const fetchServicesAndRoles = async () => {
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

  const handleServiceChange = (e) => {
    setSelectedService(e.target.value);
  };

  const handleRoleChange = (e) => {
    setSelectedRole(e.target.value);
  };

  const handlePermissionChange = (e) => {
    const { name, checked } = e.target;
    setPermissions(prevPermissions => ({
      ...prevPermissions,
      [name]: checked
    }));
  };

  const fetchPermissions = (service, role) => {
    // Simulate fetching permissions from an API
    // Replace with actual API call
    const fetchedPermissions = {
      read: true,
      write: false,
      delete: true
    };
    setPermissions(fetchedPermissions);
  };

  return (
    <>
      <h1>Permissions</h1>
      <div>
        <label htmlFor="services">Select Service:</label>
        <select id="services" value={selectedService} onChange={handleServiceChange}>
          <option value="">Select a service</option>
          {Object.entries(services).map(([key, value]) => (
            <option key={key} value={value}>{value}</option>
          ))}
        </select>
      </div>
      <div>
        <label htmlFor="roles">Select Role:</label>
        <select id="roles" value={selectedRole} onChange={handleRoleChange}>
          <option value="">Select a role</option>
          {Object.entries(roles).map(([key, value]) => (
            <option key={key} value={value}>{value}</option>
          ))}
        </select>
      </div>
      <div>
        {areSelected && (
          <div>
            <h2>{selectedService} - {selectedRole}: Permissions Details</h2>
            <form>
              {Object.entries(permissions).map(([key, value]) => (
                <div key={key}>
                  <label>
                    <input
                      type="checkbox"
                      name={key}
                      checked={value}
                      onChange={handlePermissionChange}
                    />
                    {key}
                  </label>
                </div>
              ))}
            </form>
          </div>
        )}
      </div>
    </>
  );
}

export default DisplayPermissions
