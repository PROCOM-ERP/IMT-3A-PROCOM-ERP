import React from 'react';
import Navbar from '../components/Navbar';
import "../css/App.css";
import CheckUserConnection from '../components/CheckUserConnection';

function Home() {
  return (
    <>
      <CheckUserConnection />
      <Navbar />
      <div className='title'>Home</div>
    </>
  )
}

export default Home;
