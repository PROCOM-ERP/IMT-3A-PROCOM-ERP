import React from "react";
import CheckAdminConnection from "../../components/CheckAdminConnection"; // Importing CheckAdminConnection component
import Navbar from "../../components/Navbar"; // Importing Navbar component
import UserForm from "../../components/admin/UserForm"; // Importing AddUserForm component

function AddUser() {
  return (
    <>
      <CheckAdminConnection /> {/* Render the CheckAdminConnection component to check jwt exists and if user is admin */}
      <Navbar navUser="admin" /> {/* Render the Navbar component with the prop navUser='admin' to manage navbar content and color */}
      <UserForm title="Add a User" /> {/* Render the AddUserForm component with the prop title */}
    </>
  );
}

export default AddUser;
