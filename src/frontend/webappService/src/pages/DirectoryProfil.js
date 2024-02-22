import React from 'react';
import { useParams } from 'react-router-dom';
import Navbar from '../components/Navbar';
import UserProfil from '../components/UserProfil';

function DirectoryProfil() {
  const props = useParams();
  const userId = props.userId;
  return (
    <>
      <Navbar />
      <UserProfil title="User Profil" userId={userId} />
    </>
  )
}

export default DirectoryProfil;
