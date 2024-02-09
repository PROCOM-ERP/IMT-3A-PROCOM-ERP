import React from "react";
import Navbar from "../components/Navbar";
import "../css/App.css";
import UserProfil from "../components/UserProfil.js";

function Profil() {
  const userId = localStorage.getItem("id");
  return (
    <>
      <Navbar />
      <UserProfil userId={userId} />
    </>
  );
}

export default Profil;
