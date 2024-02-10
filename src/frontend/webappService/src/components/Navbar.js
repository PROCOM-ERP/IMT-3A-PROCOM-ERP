import '../css/Navbar.css'
import React from 'react'


function Navbar({ navUser = "user" }) { // navUSer can be user or admin
    const companyName = "ERP FIP";
    let isAdmin = false;
    if (navUser === "admin") isAdmin = true;

    const getNavTitle = () => {
        if (isAdmin) {
            return (
                <>
                    <li>
                        <a href="/admin">Home</a>
                    </li>
                    <li>
                        <a href="/adminPermissions">Permissions</a>
                    </li>
                    <li>
                        <a href="/adminDirectory">Users</a>
                    </li>
                </>
            )
        } else {
            return (
                <>
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
                </>
            )
        }
    }


    return (
        <>
            <nav className={` navigation ${navUser} `} > {/* navUser is used to set the right navbar color */}
                <a href="/" className="brand-name">{companyName}</a>
                <div className={`navigation-menu ${navUser}`}>
                    <ul>
                        {getNavTitle()}
                    </ul>
                </div>
            </nav>
        </>
    )
}

export default Navbar
