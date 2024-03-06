import React from "react";
import ProfilForm from "../components/ProfilForm.js";
import Navbar from "../components/Navbar";
import "../css/App.css";
import CheckUserConnection from "../components/CheckUserConnection.js";

function ModifyProfil() {
  const userId = localStorage.getItem("id");

  return (
    <>
      <CheckUserConnection />
      <Navbar />
      <ProfilForm title="Modify Profil" userId={userId} />
    </>
  );
}

export default ModifyProfil;
