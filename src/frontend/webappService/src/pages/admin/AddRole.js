import React from 'react'; // Importing React library
import Navbar from '../../components/Navbar'; // Importing Navbar component
import CheckAdminConnection from '../../components/CheckAdminConnection'; // Importing CheckAdminConnection component
import RoleForm from '../../components/admin/RoleForm'; // Importing RoleForm component

function AddRole() {
  return (
    <>
      <CheckAdminConnection /> {/* Render the CheckAdminConnection component to check jwt exists and if user is admin */}
      <Navbar navUser='admin' /> {/* Render the Navbar component with the prop navUser='admin' to manage navbar content and color */}
      <RoleForm /> {/* Render the RoleForm component */}
    </>
  )
}

export default AddRole; // Exporting AddRole component
