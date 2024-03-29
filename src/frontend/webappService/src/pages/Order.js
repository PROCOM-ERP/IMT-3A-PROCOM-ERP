import React from 'react'
import { useParams } from 'react-router-dom';
import Navbar from '../components/Navbar'
import DisplayOrder from '../components/DisplayOrder'

function Order() {
  const props = useParams();
  const orderId = props.orderId;
  return (
    <>
      <Navbar />
      <DisplayOrder orderId={orderId} />
    </>
  )
}

export default Order
