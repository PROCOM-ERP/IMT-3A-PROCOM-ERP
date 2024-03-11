import React from "react";
import PasswordForm from "../components/PasswordForm";
import Navbar from "../components/Navbar";
import CheckUserConnection from "../components/CheckUserConnection";

function ModifyPassword() {
  return (
    <>
      <CheckUserConnection />
      <Navbar />
      <PasswordForm />
    </>
  );
}

export default ModifyPassword;
