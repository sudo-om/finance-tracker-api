import React from 'react';
import { Wallet, LayoutDashboard, Receipt, PiggyBank, Send, LogOut } from 'lucide-react';
import { removeAuthToken, getUser } from '../api';

export default function Navbar({ activeTab, setActiveTab, onLogout }) {
  const user = getUser();

  return (
    <nav className="bg-white border-b-4 border-black px-4 py-3 sticky top-0 z-50 nb-shadow-sm">
      <div className="max-w-7xl mx-auto flex flex-col md:flex-row items-center justify-between gap-4">
        {/* Brand Logo */}
        <div className="flex items-center gap-3 cursor-pointer" onClick={() => setActiveTab('dashboard')}>
          <div className="bg-[#FFE600] border-2 border-black p-2 rounded-lg nb-shadow-sm flex items-center justify-center">
            <Wallet className="w-6 h-6 text-black stroke-[3]" />
          </div>
          <div>
            <h1 className="text-2xl font-black tracking-tight text-black flex items-center gap-1">
              FIN<span className="bg-[#FFE600] px-1.5 py-0.5 border-2 border-black rounded text-black text-xl">TRACK</span>
            </h1>
          </div>
        </div>

        {/* Navigation Tabs */}
        <div className="flex items-center gap-2 overflow-x-auto w-full md:w-auto py-1">
          <button
            onClick={() => setActiveTab('dashboard')}
            className={`nb-btn ${activeTab === 'dashboard' ? 'nb-btn-primary' : 'bg-white'}`}
          >
            <LayoutDashboard className="w-4 h-4" />
            Dashboard
          </button>
          <button
            onClick={() => setActiveTab('transactions')}
            className={`nb-btn ${activeTab === 'transactions' ? 'nb-btn-primary' : 'bg-white'}`}
          >
            <Receipt className="w-4 h-4" />
            Transactions
          </button>
          <button
            onClick={() => setActiveTab('budgets')}
            className={`nb-btn ${activeTab === 'budgets' ? 'nb-btn-primary' : 'bg-white'}`}
          >
            <PiggyBank className="w-4 h-4" />
            Budgets
          </button>
          <button
            onClick={() => setActiveTab('telegram')}
            className={`nb-btn ${activeTab === 'telegram' ? 'nb-btn-success' : 'bg-white'}`}
          >
            <Send className="w-4 h-4" />
            Telegram Sync
          </button>
        </div>

        {/* User Info & Logout */}
        <div className="flex items-center gap-3">
          {user && (
            <div className="text-right hidden sm:block">
              <p className="text-sm font-black uppercase text-black">{user.firstName} {user.lastName}</p>
              <p className="text-xs font-semibold text-gray-600">{user.email}</p>
            </div>
          )}
          <button onClick={onLogout} className="nb-btn nb-btn-danger p-2" title="Logout">
            <LogOut className="w-5 h-5" />
          </button>
        </div>
      </div>
    </nav>
  );
}
