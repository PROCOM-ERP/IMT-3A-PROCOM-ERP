import React from 'react'; // Import React library
import Navbar from '../../components/Navbar'; // Import Navbar component
import DirectoryTable from '../../components/DirectoryTable'; // Import DirectoryTable component
import CheckAdminConnection from '../../components/CheckAdminConnection'; // Import CheckAdminConnection component

function AdminDirectory() {
  return (
    <>
      {/* Render the CheckAdminConnection component to check jwt exists and if user is admin */}
      <CheckAdminConnection />
      {/* Render the Navbar component with the prop navUser='admin' to manage navbar content and color */}
      <Navbar navUser='admin' />
      {/* Render the title for the admin directory page */}
      <div className='title'>Users</div>
      {/* Render the DirectoryTable component with the prop isAdmin set to true for admin directory to manage colors and content */}
      <DirectoryTable isAdmin={true} />
    </>
  );
}
export default AdminDirectory;
