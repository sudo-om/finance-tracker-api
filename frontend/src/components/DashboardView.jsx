import React, { useEffect, useState } from 'react';
import { Wallet, TrendingUp, TrendingDown, PiggyBank, Plus, ArrowUpRight, ArrowDownRight, Send } from 'lucide-react';
import { api } from '../api';

export default function DashboardView({ setActiveTab }) {
  const [balance, setBalance] = useState({ totalIncome: 0, totalExpense: 0, currentBalance: 0, savingsRate: 0 });
  const [budgets, setBudgets] = useState([]);
  const [expenses, setExpenses] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [balRes, expRes, budRes] = await Promise.all([
        api.getBalance().catch(() => ({ totalIncome: 0, totalExpense: 0, currentBalance: 0, savingsRate: 0 })),
        api.getExpenses(0, 5).catch(() => ({ content: [] })),
        api.getBudgets().catch(() => ({ content: [] })),
      ]);

      const extractedBalance = balRes?.data || balRes || {};
      setBalance({
        totalIncome: extractedBalance.totalIncome || 0,
        totalExpense: extractedBalance.totalExpense || 0,
        currentBalance: extractedBalance.currentBalance || 0,
        savingsRate: extractedBalance.savingsRate || 0,
      });

      const extractedExp = expRes?.data || expRes;
      const expList = Array.isArray(extractedExp) ? extractedExp : (extractedExp?.content || []);
      setExpenses(expList);

      const extractedBud = budRes?.data || budRes;
      const budList = Array.isArray(extractedBud) ? extractedBud : (extractedBud?.content || []);
      setBudgets(budList);
    } catch (err) {
      console.error('Error fetching dashboard data:', err);
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'OVER_BUDGET': return 'nb-badge-pink';
      case 'WARNING': return 'nb-badge-yellow';
      default: return 'nb-badge-green';
    }
  };

  return (
    <div className="space-y-8">
      {/* Top Welcome Header & Quick Actions */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-3xl font-black uppercase text-black tracking-tight">Financial Overview</h1>
          <p className="text-sm font-bold text-gray-700">Real-time balance, active budgets & transactions</p>
        </div>
        <div className="flex items-center gap-3">
          <button onClick={() => setActiveTab('transactions')} className="nb-btn nb-btn-primary">
            <Plus className="w-4 h-4 stroke-[3]" /> Add Transaction
          </button>
          <button onClick={() => setActiveTab('telegram')} className="nb-btn nb-btn-success">
            <Send className="w-4 h-4" /> Telegram Bot
          </button>
        </div>
      </div>

      {/* Summary Cards Row (3 Cards) */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* Balance Card */}
        <div className="nb-card-yellow relative overflow-hidden">
          <div className="flex items-center justify-between mb-3">
            <span className="text-xs font-black uppercase tracking-wider text-black">Current Balance</span>
            <div className="bg-black text-white p-1.5 rounded">
              <Wallet className="w-5 h-5" />
            </div>
          </div>
          <h2 className="text-4xl font-black text-black tracking-tight">₹{balance.currentBalance?.toLocaleString() || '0'}</h2>
          <div className="mt-4 flex items-center justify-between border-t-2 border-black pt-2">
            <span className="text-xs font-bold uppercase text-black">Savings Rate</span>
            <span className="nb-badge nb-badge-green">{balance.savingsRate || 0}%</span>
          </div>
        </div>

        {/* Income Card */}
        <div className="nb-card-cyan relative overflow-hidden">
          <div className="flex items-center justify-between mb-3">
            <span className="text-xs font-black uppercase tracking-wider text-black">Total Income</span>
            <div className="bg-black text-white p-1.5 rounded">
              <TrendingUp className="w-5 h-5" />
            </div>
          </div>
          <h2 className="text-4xl font-black text-black tracking-tight">₹{balance.totalIncome?.toLocaleString() || '0'}</h2>
          <div className="mt-4 flex items-center gap-1 border-t-2 border-black pt-2 text-xs font-black uppercase text-black">
            <ArrowUpRight className="w-4 h-4 text-green-800" /> Recorded Incomes
          </div>
        </div>

        {/* Expense Card */}
        <div className="nb-card-pink relative overflow-hidden">
          <div className="flex items-center justify-between mb-3">
            <span className="text-xs font-black uppercase tracking-wider text-white">Total Expenses</span>
            <div className="bg-black text-white p-1.5 rounded">
              <TrendingDown className="w-5 h-5" />
            </div>
          </div>
          <h2 className="text-4xl font-black text-white tracking-tight">₹{balance.totalExpense?.toLocaleString() || '0'}</h2>
          <div className="mt-4 flex items-center gap-1 border-t-2 border-black pt-2 text-xs font-black uppercase text-white">
            <ArrowDownRight className="w-4 h-4 text-white" /> Recorded Outflows
          </div>
        </div>
      </div>

      {/* Main Grid: Left 2/3 (Budgets & Transactions), Right 1/3 (Telegram Sync Info) */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        
        {/* Left Column (2/3) */}
        <div className="lg:col-span-2 space-y-8">
          
          {/* Active Budgets Widget */}
          <div className="nb-card space-y-4">
            <div className="flex items-center justify-between border-b-2 border-black pb-3">
              <h3 className="text-lg font-black uppercase flex items-center gap-2">
                <PiggyBank className="w-5 h-5" /> Active Budgets
              </h3>
              <button onClick={() => setActiveTab('budgets')} className="text-xs font-black uppercase underline hover:text-[#FF2A85]">
                Manage Budgets →
              </button>
            </div>

            {(!budgets || budgets.length === 0) ? (
              <p className="text-sm font-semibold text-gray-600 py-4 text-center">
                No budgets configured. Click "Manage Budgets" to set category spending limits.
              </p>
            ) : (
              <div className="space-y-4">
                {budgets.slice(0, 4).map((b) => {
                  const percent = Math.min(b.percentageUsed || 0, 100);
                  return (
                    <div key={b.id || b.category} className="border-2 border-black p-3 rounded bg-[#F4F0EA] space-y-2">
                      <div className="flex items-center justify-between text-sm font-black uppercase">
                        <span>{b.category}</span>
                        <div className="flex items-center gap-2">
                          <span className="text-xs font-bold text-gray-700">₹{b.spent || 0} / ₹{b.amount}</span>
                          <span className={`nb-badge ${getStatusColor(b.status)}`}>{b.status || 'ON_TRACK'}</span>
                        </div>
                      </div>

                      <div className="nb-progress-bg">
                        <div
                          className={`nb-progress-fill ${
                            b.status === 'OVER_BUDGET' ? 'bg-[#FF2A85]' : b.status === 'WARNING' ? 'bg-[#FFE600]' : 'bg-[#00E5FF]'
                          }`}
                          style={{ width: `${percent}%` }}
                        />
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </div>

          {/* Recent Expenses Table */}
          <div className="nb-card space-y-4">
            <div className="flex items-center justify-between border-b-2 border-black pb-3">
              <h3 className="text-lg font-black uppercase flex items-center gap-2">
                <Wallet className="w-5 h-5" /> Recent Transactions
              </h3>
              <button onClick={() => setActiveTab('transactions')} className="text-xs font-black uppercase underline hover:text-[#00E5FF]">
                View All →
              </button>
            </div>

            {(!expenses || expenses.length === 0) ? (
              <p className="text-sm font-semibold text-gray-600 py-4 text-center">
                No recent transactions recorded yet.
              </p>
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full text-left border-collapse">
                  <thead>
                    <tr className="bg-[#FFE600] border-2 border-black text-xs font-black uppercase">
                      <th className="p-2 border-r-2 border-black">Date</th>
                      <th className="p-2 border-r-2 border-black">Title / Merchant</th>
                      <th className="p-2 border-r-2 border-black">Category</th>
                      <th className="p-2">Amount</th>
                    </tr>
                  </thead>
                  <tbody>
                    {expenses.map((exp) => (
                      <tr key={exp.id} className="border-b-2 border-black text-sm font-bold bg-white">
                        <td className="p-2 border-r-2 border-black text-xs font-bold">{exp.expenseDate}</td>
                        <td className="p-2 border-r-2 border-black font-black">{exp.merchant || exp.title}</td>
                        <td className="p-2 border-r-2 border-black">
                          <span className="nb-badge nb-badge-yellow">{exp.category?.name || exp.category}</span>
                        </td>
                        <td className="p-2 font-black text-[#FF2A85]">-₹{exp.amount}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>

        </div>

        {/* Right Column (1/3): Telegram Sync Widget */}
        <div className="space-y-6">
          <div className="nb-card bg-[#FFE600] border-3 border-black space-y-4">
            <div className="flex items-center gap-2">
              <Send className="w-6 h-6 text-black" />
              <h3 className="text-xl font-black uppercase text-black">Telegram Bot</h3>
            </div>

            <p className="text-xs font-bold text-black leading-relaxed">
              Add transactions on-the-go by sending messages to your Telegram bot. Instant synchronization!
            </p>

            <div className="border-2 border-black p-3 bg-white rounded space-y-2">
              <p className="text-xs font-black uppercase text-gray-700">Quick Bot Commands:</p>
              <div className="text-xs font-bold space-y-1">
                <p><code className="bg-[#F4F0EA] px-1 border border-black rounded">/spent 450 Food Domino's</code></p>
                <p><code className="bg-[#F4F0EA] px-1 border border-black rounded">/income 50000 Salary</code></p>
                <p><code className="bg-[#F4F0EA] px-1 border border-black rounded">/balance</code></p>
                <p><code className="bg-[#F4F0EA] px-1 border border-black rounded">/budgets</code></p>
              </div>
            </div>

            <button onClick={() => setActiveTab('telegram')} className="nb-btn bg-black text-white w-full justify-center">
              Generate Telegram OTP Code →
            </button>
          </div>
        </div>

      </div>
    </div>
  );
}
