import React, { useEffect, useState } from 'react';
import Button from './Button';
import Popup from './Popup';
import { useNavigate } from 'react-router-dom';
import '../css/DisplayPermissions.css';


function DisplayPermissions() {
  const [services, setServices] = useState({});
  const [roles, setRoles] = useState({});
  const [selectedService, setSelectedService] = useState('');
  const [selectedRole, setSelectedRole] = useState('');
  const [isEnabled, setIsEnabled] = useState(false);
  const [permissions, setPermissions] = useState([]);
  const [prevIsEnabled, setPrevIsEnabled] = useState(false);
  const [prevPermissions, setPrevPermissions] = useState([]);
  const [showPopup, setShowPopup] = useState(false);
  const [popupContent, setPopupContent] = useState({});
  const navigate = useNavigate();

  const [areSelected, setAreSelected] = useState(false); // to set true if a service AND a role is selected

  const user = localStorage.getItem('id');
  const token = localStorage.getItem('Token');
  const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  };

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
    // Update the permissions array
    setPermissions(prevPermissions => {
      // Find the index of the permission with the matching name
      const index = prevPermissions.findIndex(permission => permission.name === name);
      // Create a copy of the permissions array
      const updatedPermissions = [...prevPermissions];
      // Update the isEnabled property of the permission at the found index
      updatedPermissions[index] = { ...updatedPermissions[index], isEnabled: checked };
      return updatedPermissions;
    });
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
        setPrevIsEnabled(data.isEnable);
        setPermissions(initialPermissions);
        setPrevPermissions(initialPermissions);
        console.log("[LOG] permissions retrived")
      })
      .catch(error => {
        console.error('API request error: ', error);
      });
  };


  const handleSaveChanges = () => {
    // Construct payload
    const enabledPermissions = permissions // Get a list permission names enabled
      .filter(permission => permission.isEnabled) // Filter permissions with isEnabled === true
      .map(permission => permission.name); // Extract permission names
    const payload = {
      isEnable: isEnabled,
      permissions: enabledPermissions
    };

    const apiUrl = `https://localhost:8041/api/${selectedService}/v1/roles/${selectedRole}`;

    // Send API request to update database
    fetch(apiUrl, {
      method: 'PUT',
      headers: headers,
      body: JSON.stringify(payload)
    })
      .then(response => {
        if (!response.ok) {
          throw new Error('Failed to save changes');
        }
        setShowPopup(true); // Show popup when update is successful
        setPopupContent({
          title: 'Update Permissions',
          content: 'It worked. You have been disconnected. Login.'
        });
        console.log("[LOG] Permissions updated with success");
      })
      .catch(error => {
        console.error('Error saving changes for permissions:', error);
      });
  };

  const handleResetChanges = () => {
    // Reset permissions and isEnabled to initial values
    setIsEnabled(prevIsEnabled);
    setPermissions(prevPermissions);
  };

  const handleAddRole = () => {
    navigate("/addRole");
  };

  const closePopup = () => {
    setShowPopup(false);
    navigate('/'); // Navigate to "/" page to reconnect
  };

  return (
    <>
      <div className='permissions-container'>
        <div className='select-container service'>
          <label htmlFor="services">Services</label>
          <select id="services" value={selectedService} onChange={handleServiceChange}>
            <option value="">Select</option>
            {Object.entries(services).map(([key, value]) => (
              <option key={key} value={value}>{value}</option>
            ))}
          </select>
        </div>
        <div className='select-container role'>
          <label htmlFor="roles">Roles</label>
          <div className='select-button-container'>
            <select id="roles" value={selectedRole} onChange={handleRoleChange}>
              <option value="">Select</option>
              {Object.entries(roles).map(([key, value]) => (
                <option key={key} value={value}>{value}</option>
              ))}
            </select>
            <div className='add-role-button'><Button onClick={handleAddRole}>+</Button></div>
          </div>
        </div>
        {areSelected && (
          <div className='checkbox-container'>
            <h2 className='title2'>Detailed {selectedRole} permissions in {selectedService}</h2>
            <div>
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
                  <label>
                    <input
                      type="checkbox"
                      name={permission.name}
                      checked={permission.isEnabled}
                      onChange={handlePermissionChange}
                      disabled={!isEnabled} // Disable checkbox if isEnabled is false
                    />
                    {permission.name}
                  </label>
                </div>
              ))}
              <Button onClick={handleSaveChanges}>Save</Button>
              <Button onClick={handleResetChanges}>Reset</Button>
            </div>
          </div>
        )}
        {showPopup && (
          <Popup
            title={popupContent.title}
            content={popupContent.content}
            onClose={closePopup}
          />
        )}
      </div>
    </>
  );
}

export default DisplayPermissions