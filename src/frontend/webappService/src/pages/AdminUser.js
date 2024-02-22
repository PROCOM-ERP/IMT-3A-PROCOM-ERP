import React from 'react'
import Navbar from '../components/Navbar'
import UserProfilAdmin from '../components/UserProfilAdmin'
import { useParams } from 'react-router-dom';

function AdminUser() {
  const props = useParams();
  const userId = props.userId;
  return (
    <>
      <Navbar navUser='admin' />
      <UserProfilAdmin title="User Profil" userId={userId} />
    </>
  )
}

export default AdminUser
