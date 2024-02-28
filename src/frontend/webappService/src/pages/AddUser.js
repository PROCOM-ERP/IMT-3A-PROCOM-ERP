import React from "react";
import Navbar from "../components/Navbar";
import AddUserForm from "../components/AddUserForm";

function AddUser() {
  return (
    <>
      <Navbar navUser="admin" />
      <AddUserForm title="Add a User" />
    </>
  );
}

export default AddUser;
