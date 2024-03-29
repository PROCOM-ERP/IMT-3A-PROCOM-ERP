import React from 'react'; // Import React library
import Navbar from '../../components/Navbar'; // Import Navbar component
import CheckAdminConnection from '../../components/CheckAdminConnection'; // Import CheckAdminConnection component
import DisplayHome from '../../components/DisplayHome';

function AdminHome() {
  // Render the AdminHome component
  return (
    <>
      {/* Render the CheckAdminConnection component to check jwt exists and if user is admin */}
      <CheckAdminConnection />
      {/* Render the Navbar component with the prop navUser='admin' to manage navbar content and color */}
      <Navbar navUser='admin' />
      <div className='title'>Admin Home</div>
      <DisplayHome />
    </>
  );
}
export default AdminHome;
