import React, { useState, useMemo, useEffect } from 'react';
 import './App.css';

 // --- Helper Functions ---
 const formatDate = (dateString) => {
     if (!dateString) return '';
     const date = new Date(dateString);
     return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
 };

 // --- SVG Icons ---
 const BookIcon = () => ( <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20"></path></svg> );
 const PlusCircleIcon = () => ( <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="16"></line><line x1="8" y1="12" x2="16" y2="12"></line></svg> );
 const SearchIcon = () => ( <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg> );
 const UsersIcon = () => ( <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="mr-2 h-6 w-6"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M22 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg> );

 // --- Components ---
 const AddBookModal = ({ onAddBook, onClose }) => {
     const [title, setTitle] = useState('');
     const [author, setAuthor] = useState('');
     const [genre, setGenre] = useState('');
     const handleSubmit = (e) => {
         e.preventDefault();
         if (title.trim() && author.trim() && genre.trim()) {
             onAddBook({ title, author, genre });
             onClose();
         }
     };
     return (
         <div className="modal-overlay" onClick={onClose}>
             <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                 <h2>Add a New Book</h2>
                 <form className="modal-form" onSubmit={handleSubmit}>
                     <div><label htmlFor="title">Title</label><input id="title" type="text" value={title} onChange={(e) => setTitle(e.target.value)} required /></div>
                     <div><label htmlFor="author">Author</label><input id="author" type="text" value={author} onChange={(e) => setAuthor(e.target.value)} required /></div>
                     <div><label htmlFor="genre">Genre</label><input id="genre" type="text" value={genre} onChange={(e) => setGenre(e.target.value)} placeholder="e.g., Sci-Fi, Mystery" required /></div>
                     <div className="modal-actions"><button type="button" className="cancel-button" onClick={onClose}>Cancel</button><button type="submit" className="add-button">Add Book</button></div>
                 </form>
             </div>
         </div>
     );
 };

 const AddMemberModal = ({ onAddMember, onClose }) => {
     const [name, setName] = useState('');
     const handleSubmit = (e) => {
         e.preventDefault();
         if (name.trim()) { onAddMember({ name }); onClose(); }
     };
     return (
         <div className="modal-overlay" onClick={onClose}>
             <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                 <h2>Add New Member</h2>
                 <form className="modal-form" onSubmit={handleSubmit}>
                     <div><label htmlFor="name">Member Name</label><input id="name" type="text" value={name} onChange={(e) => setName(e.target.value)} required /></div>
                     <div className="modal-actions"><button type="button" className="cancel-button" onClick={onClose}>Cancel</button><button type="submit" className="add-button">Add Member</button></div>
                 </form>
             </div>
         </div>
     );
 };

 const IssueBookModal = ({ book, members, onIssue, onClose }) => {
     const [selectedMemberId, setSelectedMemberId] = useState('');
     const handleSubmit = (e) => {
         e.preventDefault();
         if (selectedMemberId) { onIssue(book.id, selectedMemberId); onClose(); }
     };
     return (
         <div className="modal-overlay" onClick={onClose}>
             <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                 <h2>Issue Book: {book.title}</h2>
                 <form className="modal-form" onSubmit={handleSubmit}>
                     <div>
                         <label htmlFor="member">Select Member</label>
                         <select id="member" value={selectedMemberId} onChange={(e) => setSelectedMemberId(e.target.value)} required>
                             <option value="" disabled>-- Choose a member --</option>
                             {members.map(member => <option key={member.id} value={member.id}>{member.name}</option>)}
                         </select>
                     </div>
                     <div className="modal-actions"><button type="button" className="cancel-button" onClick={onClose}>Cancel</button><button type="submit" className="issue-button">Issue Book</button></div>
                 </form>
             </div>
         </div>
     );
 };

 const BookCard = ({ book, members, onIssueClick, onReturnBook, onDeleteBook }) => {
     const { id, title, author, genre, isIssued, dueDate, issuedToMemberId, fine } = book;
     const issuedMember = isIssued ? members.find(m => m.id === issuedToMemberId) : null;
     let statusClass = 'status-available';
     let statusText = 'Available';
     if (isIssued) {
         statusClass = fine > 0 ? 'status-overdue' : 'status-issued';
         statusText = `Issued to: ${issuedMember?.name || 'Unknown'}`;
     }

     return (
         <div className="book-card">
             <div>
                 <span className="genre-tag">{genre}</span>
                 <h3>{title}</h3>
                 <p>by {author}</p>
                 <span className={`status-badge ${statusClass}`}>{statusText}</span>
                 {isIssued && <p className="due-date">Due: {formatDate(dueDate)}</p>}
                 {fine > 0 && <p className="fine-alert">Fine: ₹{fine}</p>}
             </div>
             <div className="card-buttons-wrapper">
                 {isIssued ? (
                     <button onClick={() => onReturnBook(id)} className="card-button return-button">Return Book</button>
                 ) : (
                     <button onClick={() => onIssueClick(book)} className="card-button issue-button">Issue Book</button>
                 )}
                 <button onClick={() => onDeleteBook(id, title, isIssued)} className="card-button delete-button">Delete</button>
             </div>
         </div>
     );
 };

 const MembersSection = ({ members, onAddMember, onDeleteMember }) => (
     <div className="members-container">
         <div className="container-header"><h2><UsersIcon /> Members</h2><button className="add-button-small" onClick={onAddMember}><PlusCircleIcon/> Add</button></div>
         {members.length === 0 ? (<p>No members yet.</p>) : (
             <ul className="members-list">
                 {members.map(member => ( <li key={member.id}><span>{member.name}</span><button onClick={() => onDeleteMember(member.id, member.name)} className="delete-member-button">×</button></li> ))}
             </ul>
         )}
     </div>
 );

 const HistoryLog = ({ history }) => (
     <div className="history-container">
         <div className="container-header"><h2>Transaction History</h2></div>
         {history.length === 0 ? (<p>No transactions yet.</p>) : (
             <ul className="history-list">
                 {history.map((item, index) => ( <li key={index}><span className="history-title">{item.bookTitle}</span> was {item.action} by <span className="history-member">{item.memberName}</span> on {formatDate(item.timestamp)}</li> ))}
             </ul>
         )}
     </div>
 );

 export default function App() {
     const [books, setBooks] = useState([]);
     const [members, setMembers] = useState([]);
     const [history, setHistory] = useState([]);
     const [searchTerm, setSearchTerm] = useState('');
     const [selectedGenre, setSelectedGenre] = useState('All');
     const [modals, setModals] = useState({ add: false, member: false, issue: false });
     const [bookToIssue, setBookToIssue] = useState(null);
     const [error, setError] = useState(null);
     const API_URL = 'http://localhost:8080/api';

     const fetchAllData = async () => {
         try {
             const res = await Promise.all([ fetch(`${API_URL}/books`), fetch(`${API_URL}/members`), fetch(`${API_URL}/history`) ]);
             if (res.some(r => !r.ok)) throw new Error('Network response was not ok');
             const [booksData, membersData, historyData] = await Promise.all(res.map(r => r.json()));
             setBooks(booksData);
             setMembers(membersData);
             setHistory(historyData);
             setError(null);
         } catch (err) { setError('Could not connect to the server. Please ensure the Java backend is running.'); }
     };

     useEffect(() => { fetchAllData(); }, []);

     const handleApiCall = (url, options) => {
         fetch(url, options)
         .then(res => {
             if (res.status === 204) return null;
             if (!res.ok) {
                 // If the server sends a conflict error (e.g., deleting a member with books), show an alert.
                 if (res.status === 409) {
                     alert("Action failed: This member still has books issued.");
                 }
                 throw new Error(`HTTP error! status: ${res.status}`);
             }
             return res.json();
         })
         .then(fetchAllData)
         .catch(err => { console.error(err); setError('An action failed. Please check the server connection.'); });
     };

     const handleAddBook = (bookData) => handleApiCall(`${API_URL}/books`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(bookData) });
     const handleAddMember = (memberData) => handleApiCall(`${API_URL}/members`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(memberData) });
     const handleIssueBook = (bookId, memberId) => handleApiCall(`${API_URL}/books/issue`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ bookId, memberId }) });
     const handleReturnBook = (bookId) => handleApiCall(`${API_URL}/books/return`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ bookId }) });
     const handleDeleteBook = (bookId, title, isIssued) => {
         if (isIssued) { alert("Cannot delete a book that is currently issued."); return; }
         if (window.confirm(`Are you sure you want to delete "${title}"?`)) {
             handleApiCall(`${API_URL}/books/${bookId}`, { method: 'DELETE' });
         }
     };
     const handleDeleteMember = (memberId, name) => {
         if (window.confirm(`Are you sure you want to delete member "${name}"?`)) {
             handleApiCall(`${API_URL}/members/${memberId}`, { method: 'DELETE' });
         }
     };

     const genres = useMemo(() => ['All', ...new Set(books.map(b => b.genre))], [books]);

     const filteredBooks = useMemo(() => {
         return books.filter(book =>
             (selectedGenre === 'All' || book.genre === selectedGenre) &&
             (book.title.toLowerCase().includes(searchTerm.toLowerCase()) || book.author.toLowerCase().includes(searchTerm.toLowerCase()))
         );
     }, [books, searchTerm, selectedGenre]);

     return (
         <div>
             <header className="header"><div className="header-content"><h1 className="header-title"><BookIcon /> Library Management System</h1></div></header>
             <div className="main-layout">
                 <div className="left-panel">
                     <MembersSection members={members} onAddMember={() => setModals({...modals, member: true})} onDeleteMember={handleDeleteMember} />
                     <HistoryLog history={history} />
                 </div>
                 <main className="right-panel">
                     <div className="controls">
                         <div className="search-box"><SearchIcon /><input type="text" placeholder="Search by title or author..." value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} /></div>
                         <div className="filter-box">
                             <label htmlFor="genre-filter">Filter by Genre:</label>
                             <select id="genre-filter" value={selectedGenre} onChange={(e) => setSelectedGenre(e.target.value)}>
                                 {genres.map(g => <option key={g} value={g}>{g}</option>)}
                             </select>
                         </div>
                         <button className="add-button" onClick={() => setModals({...modals, add: true})}><PlusCircleIcon /> Add New Book</button>
                     </div>
                     {error && (<div className="error-box"><strong>Error: </strong><span>{error}</span></div>)}
                     <div className="book-grid">{filteredBooks.map(book => (<BookCard key={book.id} book={book} members={members} onIssueClick={(b) => { setBookToIssue(b); setModals({...modals, issue: true}); }} onReturnBook={handleReturnBook} onDeleteBook={handleDeleteBook} />))}</div>
                     {filteredBooks.length === 0 && !error && (<div style={{ textAlign: 'center', padding: '4rem 0' }}><h2>No Books Found</h2><p>{searchTerm ? "Try adjusting your search." : "The library is empty."}</p></div>)}
                 </main>
             </div>
             {modals.add && <AddBookModal onAddBook={handleAddBook} onClose={() => setModals({...modals, add: false})} />}
             {modals.member && <AddMemberModal onAddMember={handleAddMember} onClose={() => setModals({...modals, member: false})} />}
             {modals.issue && bookToIssue && <IssueBookModal book={bookToIssue} members={members} onIssue={handleIssueBook} onClose={() => setModals({...modals, issue: false})} />}
         </div>
     );
 }