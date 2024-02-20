import React, { useEffect, useState } from 'react'

function DisplayPermissions() {
  const [services, setServices] = useState({});
  const [roles, setRoles] = useState({});
  const [selectedService, setSelectedService] = useState('');
  const [selectedRole, setSelectedRole] = useState('');
  const [isEnabled, setIsEnabled] = useState(false);
  const [permissions, setPermissions] = useState([]);

  const [areSelected, setAreSelected] = useState(false); // to set true if a service AND a role is selected

  const user = localStorage.getItem('id');
  const token = localStorage.getItem('Token');
  const headers = { 'Authorization': `Bearer ${token}` };

  useEffect(() => {
    fetchServicesAndRoles();
  }, []);

  useEffect(() => {
    if (selectedService && selectedRole) {
      setAreSelected(true);
      // Fetch permissions based on selected service and role
      fetchPermissions(selectedService, selectedRole);
    } else {
      setAreSelected(false);
      setPermissions([]);
    }
  }, [selectedService, selectedRole]);

  // TODO
  const fetchServicesAndRoles = async () => {
    const apiUrl = "https://localhost:8041/api/authentication/v1/roles/microservices";
    // Make the API request
    await fetch(apiUrl, {
      method: "GET",
      headers: headers,
    })
      .then((response) => {
        if (!response.ok) throw new Error(response.status);
        const res = response.json();
        return res;
      })
      .then(data => {
        setServices(data.microservices);
        setRoles(data.roles);
        console.log("[LOG] List of services and roles retrieved");
      })
      .catch(error => {
        console.error('API request error: ', error);
      });
  }

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

  const handleIsEnabledChange = (e) => {
    const checked = e.target.checked;
    setIsEnabled(checked);
  };

  const fetchPermissions = async (service, role) => {
    // Get permissions from api thanks to service and role name
    const apiUrl = `https://localhost:8041/api/${service}/v1/roles/${role}`;
    // Make the API request
    await fetch(apiUrl, {
      method: "GET",
      headers: headers,
    })
      .then((response) => {
        if (!response.ok) throw new Error(response.status);
        const res = response.json();
        return res;
      })
      .then(data => {
        console.log(data);
        const fetchedPermissions = data.permissions;
        // Construct an array of permissions with initial isEnabled values
        const initialPermissions = fetchedPermissions.map(permission => ({
          name: permission.name,
          isEnabled: permission.isEnable
        }));

        setIsEnabled(data.isEnable);
        setPermissions(initialPermissions);
      })
      .catch(error => {
        console.error('API request error: ', error);
      });
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
            <div>
              <label>
                <input
                  type="checkbox"
                  name={"isEnabled"}
                  checked={isEnabled}
                  onChange={handleIsEnabledChange}
                />
                Is active
              </label>
            </div>

            {permissions.map((permission, index) => (
              <div key={index}>
                {console.log("permissions : " + index + " " + permission.name + " " + permission.isEnabled)}
                <label>
                  <input
                    type="checkbox"
                    name={permission.name}
                    checked={permission.isEnabled}
                    onChange={handlePermissionChange}
                  />
                  {permission.name}
                </label>
              </div>
            ))}
          </div>
        )}
      </div>
    </>
  );
}

export default DisplayPermissions