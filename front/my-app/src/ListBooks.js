import axios from 'axios';
import React from 'react';
import Book from'./Book.js';
import "./ListBooks.scss"
import './MyBooks.scss';
import { useNavigate } from "react-router-dom";

class ListBooks extends React.Component {
  constructor(){
      super();
      this.state={
          books:[]
      }
  }
  componentDidMount(){
      axios.get('/v1/books?status=FREE').then(
        (response)=>{
          this.setState({books:response.data})
        }
      )

      
  }

  borrowBook(bookId){
    axios.post(`/borrows/${bookId}`,{}).then(
      ()=>this.props.goTo('/myBorrows')
    )
  }

  render () {

    return <div className="container"><h1>Les Livres Disponibles</h1>
            <div className="list-container">
                {this.state.books.length===0? "Pas de livres disponibles":null}
                {this.state.books.map((book,key) => (
                    <div className="list-book-container" key={key}>
                      <Book title={book.title} category={book.category.label} lender={`${book.user.firstName} ${book.user.lastName}`}/>
                      <div className="text-center">
                        <button className="btn btn-primary btn-sm" onClick={()=>this.borrowBook(book.id)}>Emprunter</button>
                      </div>
                    </div>
                ))}
          </div>
    </div>
  }
}

export default function (props) {
  const goTo = useNavigate();
  return <ListBooks {...props} goTo={goTo} />;
}