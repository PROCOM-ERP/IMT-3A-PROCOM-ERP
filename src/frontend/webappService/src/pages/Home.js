import React from 'react';
import Navbar from '../components/Navbar';
import "../css/App.css";
import CheckUserConnection from '../components/CheckUserConnection';
import DisplayHome from '../components/DisplayHome';

function Home() {
  return (
    <>
      <CheckUserConnection />
      <Navbar />
      <div className='title'>Home</div>
      <DisplayHome />
    </>
  )
}

export default Home;
