import '../css/App.css';
import React from 'react';
import AuthForm from '../components/AuthForm.js';
import AuthHelloWorld from '../components/AuthHelloWorld.js';

function Authentification() {
  return (
    <div className='authentification-container'>
      <h1>Login</h1>
      <AuthForm/>
    </div>
  )
}

export default Authentification;
