import React from 'react'
import Navbar from '../components/Navbar'
import UserProfilAdmin from '../components/UserProfilAdmin'
import { useParams } from 'react-router-dom';
import CheckAdminConnection from '../components/CheckAdminConnection';

function AdminUser() {
  const props = useParams();
  const userId = props.userId;
  return (
    <>
      <CheckAdminConnection />
      <Navbar navUser='admin' />
      <UserProfilAdmin title="User Profil" userId={userId} />
    </>
  )
}

export default AdminUser
