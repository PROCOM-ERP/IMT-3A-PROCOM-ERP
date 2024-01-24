import '../css/Navbar.css'
import React from 'react'


function Navbar() {
  return (
    <>
    <nav className='navigation'>
        <a href="/home" className="brand-name">
            ERP FIP
        </a>
        <div className="navigation-menu">
            <ul>
                <li>
                    <a href="/home">Home</a>
                </li>
                <li>
                    <a href="/profil">Profil</a>
                </li>
            </ul>
      </div>
    </nav>
    </>
  )
}

export default Navbar
