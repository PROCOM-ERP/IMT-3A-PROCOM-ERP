import React from "react";
import ProfilForm from "../components/ProfilForm.js";
import Navbar from "../components/Navbar";
import "../css/App.css";

function ModifyProfil() {
  const userId = localStorage.getItem("id");

  return (
    <>
      <Navbar />
      <ProfilForm title="Modify Profil" userId={userId} />
    </>
  );
}

export default ModifyProfil;
