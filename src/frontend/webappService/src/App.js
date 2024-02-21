import './css/App.css';
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Authentification from './pages/Authentification.js';
import Home from './pages/Home.js';
import Profil from './pages/Profil.js';
import Directory from './pages/Directory.js';
import Inventory from './pages/Inventory.js';
import OrderManagement from './pages/OrderManagement.js';
import DirectoryProfil from './pages/DirectoryProfil.js';
import AdminUsers from './pages/AdminUsers.js';
import AdminHome from './pages/AdminHome.js';
import AdminPermissions from './pages/AdminPermissions.js';
import ModifyProfil from './pages/ModifyProfil.js';
import AddRole from './pages/AddRole.js';

function App() {
  return (
    <>
      <Router>
        <Routes>
          <Route path='/' exact Component={Authentification} />
          <Route path='/home' exact Component={Home} />
          <Route path='/directory' exact Component={Directory} />
          <Route path='/inventory' exact Component={Inventory} />
          <Route path='/orderManagement' exact Component={OrderManagement} />
          <Route path='/profil/' exact Component={Profil} />
          <Route path='/user/:userId' exact Component={DirectoryProfil} />
          <Route path='/admin' exact Component={AdminHome} />
          <Route path='/adminPermissions' exact Component={AdminPermissions} />
          <Route path='/adminDirectory' exact Component={AdminUsers} />
          <Route path='/modifyProfil' exact Component={ModifyProfil} />
          <Route path='/addRole' exact Component={AddRole} />
        </Routes>
      </Router>
    </>
  );
}

export default App;
