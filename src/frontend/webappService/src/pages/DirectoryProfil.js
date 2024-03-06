import React from 'react';
import { useParams } from 'react-router-dom';
import Navbar from '../components/Navbar';
import UserProfil from '../components/UserProfil';
import CheckUserConnection from '../components/CheckUserConnection';

function DirectoryProfil() {
  const props = useParams();
  const userId = props.userId;
  return (
    <>
      <CheckUserConnection />
      <Navbar />
      <UserProfil title="User Profil" userId={userId} />
    </>
  )
}

export default DirectoryProfil;
