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
  const getPermissions = async () => {
    console.log("areSelected: ", areSelected);
    console.log("Service selected: ", selectedService);
    console.log("Role selected: ", selectedRole);

    if (areSelected === true) {
      setPermissions({ "read": false, "write": false, "delete": false });
      // setSelectedRole('');
      // setSelectedService('');
    } else if (Object.keys(permissions).length > 0) {
      setPermissions({});
    }

    // const apiUrl = "https://localhost:8041/api/auth/v1/auth/jwt"; // TODO: to change
    // // Make the API request
    // await fetch(apiUrl, {
    //   method: "GET",
    //   headers: headers,
    // })
    //   .then((response) => {
    //     if (!response.ok) throw new Error(response.status);
    //     const res = response.json();
    //     return res;
    //   })
    //   .then(data => {
    //     setPermissions(data);
    //     console.log("[LOG] Permissions retrieved");
    //   })
    //   .catch(error => {
    //     console.error('API request error: ', error);
    //   });

  }

  const handleSelection = () => {
    if (selectedService.length > 0 && selectedRole.length > 0) {
      setAreSelected(true);
      setSelectedRole('');
      setSelectedService('');
    } else {
      setAreSelected(false);
    }
  }

  const handleServiceChange = (e) => {
    setSelectedService(e.target.value);
    handleSelection();
  };

  const handleRoleChange = (e) => {
    setSelectedRole(e.target.value);
    handleSelection();
  };

  const handlePermissionChange = (e) => {
    const { name, checked } = e.target;
    setPermissions(prevPermissions => ({
      ...prevPermissions,
      [name]: checked
    }));
  };

  const displayPermissions = () => {
    //handleSelect();
    //console.log("display permissions: ", areSelected)
    // if (areSelected) {
    getPermissions();
    console.log("Permissions: ", (Object.keys(permissions).length > 0));
    console.log(permissions);

    if (Object.keys(permissions).length > 0) {

      return (
        <>
          <div>
            <h2> {selectedService}-{selectedRole}: Permissions Details</h2>
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
        </>
      )

    } else {
      return (
        <>
          <p>Select a service and a role to display permissions.</p>
        </>
      )
    }
  }

  useEffect(() => {
    // getServicesAndRoles();
    // getPermissions();
    // console.log("areSelected: ", areSelected);
    // displayPermissions();
    handleSelection();
  }, []);

  return (
    <>
      <h1 >Permissions</h1>
      <div>
        <label htmlFor="services">Select Service: </label>
        <select id="services" value={selectedService} onChange={handleServiceChange}>
          <option value="">Select a service</option>
          {Object.entries(services).map(([key, value]) => (
            <option key={key} value={value} >{value}</option>
          ))}
        </select>
      </div>
      <div>
        <label htmlFor="roles">Select Role:</label>
        <select id="roles" value={selectedRole} onChange={handleRoleChange}>
          <option value="">Select a role</option>
          {Object.entries(roles).map(([key, value]) => (
            <option key={key} value={value} > {value} </option>
          ))}
        </select>
      </div>
      <div>
        <p>Selected Service: {selectedService}</p>
        <p>Selected Role: {selectedRole}</p>
      </div>
      <div>
        {displayPermissions()}
      </div>

    </>
  )
}

export default DisplayPermissions
