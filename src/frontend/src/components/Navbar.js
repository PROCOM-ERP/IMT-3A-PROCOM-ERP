import '../css/Navbar.css'
import React from 'react'


function Navbar() {
    const companyName = "ERP FIP";
  return (
    <>
    <nav className='navigation'>
        <a href="/" className="brand-name">{companyName}</a>
        <div className="navigation-menu">
            <ul>
                <li>
                    <a href="/home">Home</a>
                </li>
                <li>
                    <a href="/directory">Directory</a>
                </li>
                <li>
                    <a href="/inventory">Inventory</a>
                </li>
                <li>
                    <a href="/orderManagement">Order Management</a>
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
