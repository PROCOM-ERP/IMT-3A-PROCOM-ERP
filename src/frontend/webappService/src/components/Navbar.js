import '../css/Navbar.css';
import React, { useEffect, useState } from 'react';

function Navbar({ navUser = "user" }) {
    const companyName = "ERP FIP";
    const [isAdmin, setIsAdmin] = useState(false);

    // UseEffect hook to set isAdmin state when navUser changes
    useEffect(() => {
        setIsAdmin(navUser === "admin");
    }, [navUser]);

    const getNavTab = () => {
        // Determine the navigation links based on user role
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
            );
        } else {
            return (
                <>
                    <li>
                        <a href="/home">Home</a>
                    </li>
                    <li>
                        <a href="/directory">Directory</a>
                    </li>
                    {/* <li><a href="/inventory">Inventory</a></li> */}
                    <li>
                        <a href="/orderManagement">Order Management</a>
                    </li>
                    <li>
                        <a href="/profil">Profil</a>
                    </li>
                </>
            );
        }
    };

    return (
        <>
            <nav className={`navigation ${navUser}`}>
                <a href="/" className="brand-name">{companyName}</a>
                <div className="navigation-menu">
                    <ul>
                        {getNavTab()}
                    </ul>
                </div>
            </nav>
        </>
    );
}

export default Navbar;
