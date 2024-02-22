import React from 'react';
import RoleForm from '../components/RoleForm';
import Navbar from '../components/Navbar';

function AddRole() {
  return (
    <>
      <Navbar navUser='admin' />
      <RoleForm />
    </>
  )
}

export default AddRole
