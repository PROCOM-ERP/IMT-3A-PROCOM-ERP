import '../css/App.css';
import React from 'react';
import AuthForm from '../components/AuthForm.js';
import AuthHelloWorld from '../components/AuthHelloWorld.js';
import DirHelloWorld from '../components/DirHelloWorld.js';

function Authentification() {
  return (
    <>
    <AuthHelloWorld/>
    <DirHelloWorld/>
    <AuthForm/>
    </>
  )
}

export default Authentification;
