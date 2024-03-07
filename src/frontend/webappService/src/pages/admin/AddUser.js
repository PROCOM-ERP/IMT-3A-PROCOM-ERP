import React from "react";
import Navbar from "../../components/Navbar"; // Importing Navbar component
import AddUserForm from "../../components/AddUserForm"; // Importing AddUserForm component
import CheckAdminConnection from "../../components/CheckAdminConnection"; // Importing CheckAdminConnection component

function AddUser() {
  return (
    <>
      <CheckAdminConnection /> {/* Render the CheckAdminConnection component to check jwt exists and if user is admin */}
      <Navbar navUser="admin" /> {/* Render the Navbar component with the prop navUser='admin' to manage navbar content and color */}
      <AddUserForm title="Add a User" /> {/* Render the AddUserForm component with the prop title */}
    </>
  );
}

export default AddUser;
