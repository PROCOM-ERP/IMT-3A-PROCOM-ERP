import '../css/App.css';
import React from 'react';
import AuthForm from '../components/AuthForm.js';
import AuthHelloWorld from '../components/AuthHelloWorld.js';

function Authentification() {
  return (
    <>
    <AuthHelloWorld/>
    <AuthForm/>
    </>
  )
}

export default Authentification;
