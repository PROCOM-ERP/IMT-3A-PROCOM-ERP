import React from 'react'; // Import React library
import Navbar from '../../components/Navbar'; // Import Navbar component
import CheckAdminConnection from '../../components/CheckAdminConnection'; // Import CheckAdminConnection component

function AdminHome() {
  // Render the AdminHome component
  return (
    <>
      {/* Render the CheckAdminConnection component -> check jwt exists and if user is admin*/}
      <CheckAdminConnection />
      {/* Render the Navbar component with the prop navUser='admin' -> manage navbar content and color */}
      <Navbar navUser='admin' />
    </>
  );
}
export default AdminHome;
