import React from 'react';
import RoleForm from '../components/RoleForm';
import Navbar from '../components/Navbar';
import CheckAdminConnection from '../components/CheckAdminConnection';

function AddRole() {
  return (
    <>
      <CheckAdminConnection />
      <Navbar navUser='admin' />
      <RoleForm />
    </>
  )
}

export default AddRole
