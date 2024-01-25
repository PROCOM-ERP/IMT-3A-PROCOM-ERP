import './css/App.css';
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Authentification from './pages/Authentification.js';
import Home from './pages/Home.js';
import Profil from './pages/Profil.js';
import Directory from './pages/Directory.js';
import Inventory from './pages/Inventory.js';
import OrderManagement from './pages/OrderManagement.js';

function App() {
  return (
    <div>
      <Router>
        <Routes>
          <Route path='/' exact Component={Authentification} />
          <Route path='/home' exact Component={Home} />
          <Route path='/directory' exact Component={Directory} />
          <Route path='/inventory' exact Component={Inventory} />
          <Route path='/orderManagement' exact Component={OrderManagement} />
          <Route path='/profil' exact Component={Profil} />
        </Routes>
      </Router>
    </div>
  );
}

export default App;
