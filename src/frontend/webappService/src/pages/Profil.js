import React from "react";
import Navbar from "../components/Navbar";
import "../css/App.css";
import UserProfil from "../components/UserProfil.js";
import Button from "../components/Button.js";

function Profil() {
  const userId = localStorage.getItem("id");
  return (
    <>
      <Navbar />
      <UserProfil title="Profil" userId={userId} />
    </>
  );
}

export default Profil;
