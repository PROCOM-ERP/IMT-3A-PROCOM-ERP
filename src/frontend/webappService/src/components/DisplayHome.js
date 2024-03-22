import React from 'react';
import "../css/Home.css";

function DisplayHome() {
  return (
    <>
      <div className='home-container'>
        <div className='home-image-container'>
          <img className='poster' src="/images/poster.jpg" alt="Poster" />
        </div>
        <div className='home-image-container'>
          <img className='scenario' src="/images/scenario.png" alt="Scenario" />
        </div>
      </div>
    </>
  )
}

export default DisplayHome
