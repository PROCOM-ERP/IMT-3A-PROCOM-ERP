import React from "react";
import Navbar from "../components/Navbar";
import AddUserForm from "../components/AddUserForm";
import CheckAdminConnection from "../components/CheckAdminConnection";

function AddUser() {
  return (
    <>
      <CheckAdminConnection />
      <Navbar navUser="admin" />
      <AddUserForm title="Add a User" />
    </>
  );
}

export default AddUser;
