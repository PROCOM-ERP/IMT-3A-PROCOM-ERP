import React from 'react'; // Import React library
import Navbar from '../../components/Navbar'; // Import Navbar component
import DisplayPermissions from '../../components/DisplayPermissions'; // Import DisplayPermissions component
import CheckAdminConnection from '../../components/CheckAdminConnection'; // Import CheckAdminConnection component

function AdminPermissions() {
  return (
    <>
      <CheckAdminConnection /> {/* Render the CheckAdminConnection component to check jwt exists and if user is admin */}
      <Navbar navUser='admin' /> {/* Render the Navbar component with the prop navUser='admin' to manage navbar content and color */}
      <div className='title'>Permissions Management</div> {/* Render the title for the permissions management page */}
      <DisplayPermissions />  {/* Render the DisplayPermissions component to display permissions */}
    </>
  );
}

export default AdminPermissions;
